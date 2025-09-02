package conventions.enforcer.rules

import conventions.enforcer.impl.violations.AppliedPluginViolation
import conventions.enforcer.impl.visitors.ApplyPluginVisitor
import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.DefaultVisitorManager
import conventions.enforcer.impl.rules.UnsupportedPluginRule
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class UnsupportedPluginRuleTest {

    private lateinit var visitorManager: VisitorManager
    private val astBuilder = AstBuilder()

    @BeforeEach
    fun setup() {
        visitorManager = DefaultVisitorManager()
    }

    private fun visitGroovyCode(code: String): ApplyPluginVisitor {
        val visitor = ApplyPluginVisitor(
            appliedPluginRules = emptyList(),
            visitorManager = visitorManager
        )

        val nodes = astBuilder.buildFromString(CompilePhase.CONVERSION, code)
        nodes.forEach { it.visit(visitor) }
        return visitor
    }

    @Test
    fun `flags disallowed plugins`() {
        val code = """
            plugins {
                id 'java'
                id 'some.custom.plugin'
            }
        """.trimIndent()

        val visitor = visitGroovyCode(code)

        val rule = UnsupportedPluginRule(
            visitorManager = visitorManager,
            allowedPlugins = setOf("java", "kotlin")
        )

        val result = rule.enforce(Paths.get("build.gradle"), visitor)

        assertEquals(1, result.size)
        val violation = result.first() as AppliedPluginViolation
        assertTrue(violation.message.contains("some.custom.plugin"))
    }

    @Test
    fun `does not flag allowed plugins`() {
        val code = """
            plugins {
                id 'java'
                id 'kotlin'
            }
        """.trimIndent()

        val visitor = visitGroovyCode(code)

        val rule = UnsupportedPluginRule(
            visitorManager = visitorManager,
            allowedPlugins = setOf("java", "kotlin")
        )

        val result = rule.enforce(Paths.get("build.gradle"), visitor)

        assertTrue(result.isEmpty())
    }
}