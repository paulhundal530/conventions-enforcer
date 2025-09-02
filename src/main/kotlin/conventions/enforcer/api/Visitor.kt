package conventions.enforcer.api

import org.codehaus.groovy.ast.CodeVisitorSupport

/**
 * Base class for AST visitors used to enforce a set of convention rules.
 *
 * Each visitor is tied to one or more rules and traverses the Groovy AST to collect
 * relevant data or node locations. Violations are detected based on the visitor’s findings.
 *
 * @property rules The list of rules this visitor is responsible for enforcing.
 * @property visitorManager A shared coordinator for tracking visited nodes across visitors.
 */
abstract class Visitor(
    val rules: List<Rule>,
    private val visitorManager: VisitorManager
) : CodeVisitorSupport() {

    /**
     * Registers a visited node for this visitor.
     *
     * @param node     A logical identifier for the visited node (e.g., plugin ID, dependency).
     * @param position The position (e.g., line number) in the build file where the node appears.
     */
    fun add(node: String, position: Int) {
        visitorManager.add(this.javaClass, node, position)
    }

    /**
     * Clears any internal state held by the visitor.
     *
     * Override this if your visitor needs to reset state between build file evaluations.
     */
    open fun clear() {}
}