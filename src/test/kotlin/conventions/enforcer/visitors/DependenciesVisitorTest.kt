package conventions.enforcer.visitors

import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.visitors.DependenciesVisitor
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class DependenciesVisitorTest {

    private val astBuilder = AstBuilder()

    @Test
    fun `records dependencies inside dependencies block`() {
        val source = """
            dependencies {
                implementation("com.squareup.retrofit2:retrofit:2.9.0")
                api("androidx.core:core-ktx:1.10.0")
            }
        """.trimIndent()

        val visitorManager = VisitorManager.create()
        val visitor = DependenciesVisitor(
            dependencyRules = emptyList(),
            visitorManager = visitorManager
        )

        val nodes: List<ASTNode> = astBuilder.buildFromString(CompilePhase.CONVERSION, source)
        nodes.forEach { it.visit(visitor) }

        val visited = visitorManager.visitedNotes[visitor.javaClass] ?: emptyMap()

        assertTrue("Expected 'implementation' to be visited") {
            visited.keys.any { it.contains("retrofit") }
        }

        assertTrue("Expected 'api' to be visited") {
            visited.keys.any { it.contains("core-ktx") }
        }
    }

    @Test
    fun `does not record method calls outside dependencies block`() {
        val source = """
            android {
                compileSdk = 33
            }

            implementation("com.example:outside:1.0")
        """.trimIndent()

        val visitorManager = VisitorManager.create()
        val visitor = DependenciesVisitor(
            dependencyRules = emptyList(),
            visitorManager = visitorManager
        )

        val nodes: List<ASTNode> = astBuilder.buildFromString(CompilePhase.CONVERSION, source)
        nodes.forEach { it.visit(visitor) }

        val visited = visitorManager.visitedNotes[visitor.javaClass] ?: emptyMap()

        assertEquals(emptyMap(), visited, "Expected no dependencies to be recorded")
    }

    @Test
    fun `records multiple dependencies`() {
        val source = """
            dependencies {
                implementation("com.foo:bar:1.0")
                implementation("com.baz:qux:2.0")
            }
        """.trimIndent()

        val visitorManager = VisitorManager.create()
        val visitor = DependenciesVisitor(
            dependencyRules = emptyList(),
            visitorManager = visitorManager
        )

        val nodes = astBuilder.buildFromString(CompilePhase.CONVERSION, source)
        nodes.forEach { it.visit(visitor) }

        val visited = visitorManager.visitedNotes[visitor.javaClass] ?: emptyMap()

        assertEquals(2, visited.size, "Expected 2 dependencies to be recorded")
        assertTrue(visited.keys.any { it.contains("bar") })
        assertTrue(visited.keys.any { it.contains("qux") })
    }
}