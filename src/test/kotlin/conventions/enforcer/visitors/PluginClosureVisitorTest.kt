package conventions.enforcer.visitors

import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.visitors.AllowedClosuresVisitor
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import kotlin.test.Test
import kotlin.test.assertTrue

class AllowedClosuresVisitorTest {

    private val astBuilder = AstBuilder()

    @Test
    fun `it records disallowed closure blocks`() {
        val source = """
            plugins {
                id 'java'
            }

            buildscript {
                repositories {
                    mavenCentral()
                }
            }
        """.trimIndent()

        val visitorManager = VisitorManager.create()
        val visitor = AllowedClosuresVisitor(
            closureRules = emptyList(), // we don't evaluate rules here
            visitorManager = visitorManager,
            allowList = setOf("plugins") // plugins are allowed, buildscript is not
        )

        val nodes: List<ASTNode> = astBuilder.buildFromString(CompilePhase.CONVERSION, source)
        nodes.forEach { it.visit(visitor) }

        val visited = visitorManager.visitedNotes[visitor.javaClass] ?: emptyMap()

        assertTrue("buildscript should be recorded as a disallowed closure") {
            "buildscript" in visited
        }
        assertTrue("plugins should not be recorded") {
            "plugins" !in visited
        }
    }

    @Test
    fun `it skips nested closures inside allowed methods`() {
        val source = """
            plugins {
                id 'java'
                kotlin("jvm")
            }

            repositories {
                mavenCentral()
            }
        """.trimIndent()

        val visitorManager = VisitorManager.create()
        val visitor = AllowedClosuresVisitor(
            closureRules = emptyList(),
            visitorManager = visitorManager,
            allowList = setOf("plugins")
        )

        val nodes = astBuilder.buildFromString(CompilePhase.CONVERSION, source)
        nodes.forEach { it.visit(visitor) }

        val visited = visitorManager.visitedNotes[visitor.javaClass] ?: emptyMap()

        assertTrue("repositories should be recorded") {
            "repositories" in visited
        }

        assertTrue("plugins should not be recorded") {
            "plugins" !in visited
        }
    }
}
