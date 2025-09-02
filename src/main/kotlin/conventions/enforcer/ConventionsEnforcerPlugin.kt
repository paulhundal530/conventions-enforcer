package conventions.enforcer

import conventions.enforcer.api.AstTraverser
import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.VisitationInput
import conventions.enforcer.impl.VisitorWarehouse
import conventions.enforcer.util.ReportPublisher
import org.codehaus.groovy.ast.builder.AstBuilder
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.ExtensionAware
import java.io.File

class ConventionsEnforcerPlugin : Plugin<Settings> {
    private val logger = Logging.getLogger(ConventionsEnforcerPlugin::class.java)

    override fun apply(settings: Settings) {
        val extension = settings.extensions.create(
            "conventionsEnforcer",
            ConventionsEnforcerExtension::class.java
        )

        (extension as ExtensionAware).extensions.create(
            "rules",
            RulesExtension::class.java
        )

        settings.gradle.rootProject { rootProject ->
            rootProject.tasks.register("enforceConventions") {
                it.group = "verification"
                it.description = "Enforces plugin and dependency conventions across all projects"

                it.doLast {
                    val rules = (extension as ExtensionAware)
                        .extensions.getByName("rules") as RulesExtension

                    val excluded = extension.excludedBuildFiles.getOrElse(emptyList()).toSet()
                    val allBuildFiles = rootProject.allprojects
                        .map { it.buildFile.toPath() }
                        .filterNot { path ->
                            excluded.any { exclusion -> path.toString().endsWith(exclusion) }
                        }

                    val manager = VisitorManager.create()
                    val visitors = VisitorWarehouse.create(
                        VisitationInput(
                            allowlistClosures = rules.allowedClosures.orNull?.toSet().orEmpty(),
                            allowlistPlugins = rules.allowedPlugins.orNull?.toSet().orEmpty(),
                            bannedDependencies = rules.bannedDependencies.orNull?.toSet().orEmpty(),
                            visitorManager = manager
                        )
                    )

                    val traverser = AstTraverser.create(
                        astBuilder = AstBuilder(),
                        logger = logger,
                        visitors = visitors,
                        visitorManager = manager
                    )

                    val violations = traverser.traverse(allBuildFiles)
                    val reportDir = rootProject.layout.buildDirectory
                        .dir("reports/conventions-enforcer").get().asFile
                    val reportFile = File(reportDir, "report.html")
                    reportDir.mkdirs()

                    ReportPublisher().publish(violations, reportFile)

                    val groupedViolations = violations
                        .groupBy { it::class.simpleName ?: "UnknownRule" }

                    groupedViolations.entries.take(10).forEach { (ruleName, group) ->
                        println("❌ Rule Violation: ${ruleName.replace("Violation", "").replace(Regex("([a-z])([A-Z])"), "$1 $2")}")
                        group.forEach { v ->
                            val line = Regex(""".*:(\d+)""").find(v.message)?.groupValues?.get(1) ?: "?"
                            val relativePath = v.buildFile.toAbsolutePath().normalize()
                                .let { settings.rootDir.toPath().relativize(it) }

                            println("   - /$relativePath:$line -> ${v.message.substringAfter(": ").trim()}")
                        }
                    }
                    if (violations.size > 10) {
                        println("⚠️ ${violations.size - 10} more violations omitted.")
                    }

                    println("📄 Full violation report written to: ${reportFile.absolutePath}")

                    if (extension.shouldFailOnViolation.getOrElse(true) && violations.isNotEmpty()) {
                        error("ConventionsEnforcer detected ${violations.size} violations.")
                    }
                }
            }
        }
    }
}