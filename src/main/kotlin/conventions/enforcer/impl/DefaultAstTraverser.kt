package conventions.enforcer.impl

import conventions.enforcer.api.AstTraverser
import conventions.enforcer.api.Violation
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.gradle.api.logging.Logger
import java.nio.file.Path
import java.util.TreeSet
import kotlin.io.path.readText

/**
 * Default implementation of [AstTraverser] that parses build files using the Groovy AST
 * and dispatches rule visitors.
 */
internal class DefaultAstTraverser(
    private val astBuilder: AstBuilder,
    private val visitors: List<Visitor>,
    private val visitorManager: VisitorManager,
    private val logger: Logger
) : AstTraverser {

    private val rulesBroken: TreeSet<Violation> = TreeSet()
    private val overlookedFiles: MutableSet<Path> = mutableSetOf()

    override val overlooked: Set<Path>
        get() = overlookedFiles.toSet()

    override fun traverse(buildFiles: List<Path>): Set<Violation> {
        buildFiles.forEach { buildFile ->
            val nodes: List<ASTNode> = try {
                getAstNodes(buildFile)
            } catch (ex: Exception) {
                logger.warn("⚠️ Unable to parse build file $buildFile", ex)
                overlookedFiles.add(buildFile)
                emptyList()
            }

            // Visit all AST nodes using each visitor
            visitors.forEach { visitor ->
                nodes.forEach { node -> node.visit(visitor) }
                visitor.clear()
            }

            // Let rules analyze visited nodes to generate violations
            visitors.forEach { visitor ->
                visitor.rules.forEach { rule ->
                    rulesBroken.addAll(rule.enforce(buildFile, visitor))
                }
            }

            // Clear state after each file
            visitorManager.clear()
        }

        return rulesBroken.toSet()
    }

    private fun getAstNodes(buildFile: Path): List<ASTNode> {
        val fileText = buildFile.readText()
        if (fileText.isBlank()) return emptyList()
        return astBuilder.buildFromString(CompilePhase.CONVERSION, fileText)
    }
}