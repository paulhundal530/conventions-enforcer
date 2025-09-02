package conventions.enforcer.visitors

import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.visitors.ApplyPluginVisitor
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ApplyPluginVisitorTest {

    private val visitorManager = VisitorManager.create()

    private fun visitGroovyCode(code: String): Map<String, List<Int>> {
        val nodes = AstBuilder().buildFromString(CompilePhase.CONVERSION, false, code)
        val visitor = ApplyPluginVisitor(emptyList(), visitorManager)

        nodes.forEach { node ->
            when (node) {
                is org.codehaus.groovy.ast.stmt.BlockStatement -> {
                    node.statements.forEach { stmt -> stmt.visit(visitor) }
                }
                is org.codehaus.groovy.ast.stmt.Statement -> {
                    node.visit(visitor)
                }
            }
        }

        return visitorManager.visitedNotes[visitor.javaClass] ?: emptyMap()
    }

    @Test
    fun `captures plugins from plugins block`() {
        val code = """
            plugins {
                id 'java'
                id 'kotlin'
            }
        """
        val visited = visitGroovyCode(code)
        assertTrue("java" in visited)
        assertTrue("kotlin" in visited)
    }

    @Test
    fun `captures plugin from apply plugin`() {
        val code = """
            apply plugin: 'com.foo.bar'
        """
        val visited = visitGroovyCode(code)
        assertTrue("com.foo.bar" in visited)
    }

    @Test
    fun `captures plugin from apply from`() {
        val code = """
            apply from: 'script.gradle'
        """
        val visited = visitGroovyCode(code)
        assertTrue("script.gradle" in visited)
    }
}
