package conventions.enforcer.api

import conventions.enforcer.impl.DefaultVisitorManager

/**
 * A shared state tracker used by all visitors during AST traversal.
 *
 * Tracks which nodes were visited, by which visitor, and at what positions.
 * This allows enforcement logic to avoid duplicates and coordinate between rules.
 */
interface VisitorManager {

    /**
     * A map from visitor class → visited node key → list of positions.
     *
     * Example:
     * PluginBlockVisitor → "kotlin" → [12, 14]
     */
    val visitedNotes: Map<Class<out Visitor>, VisitedNodes>

    /**
     * Registers a new visited node for a given visitor.
     *
     * @param visitor The class of the visitor reporting the visit.
     * @param key     A logical name for the node (e.g., "kotlin" plugin).
     * @param value   A position (e.g., line number or index) where the node was seen.
     */
    fun add(visitor: Class<out Visitor>, key: String, value: Int)

    /**
     * Clears all visited state.
     */
    fun clear()

    companion object {
        /**
         * Creates a default in-memory visitor manager.
         */
        fun create(): VisitorManager = DefaultVisitorManager()
    }
}

/**
 * A map from node name (e.g., plugin ID) to a list of positions it appeared at.
 */
typealias VisitedNodes = MutableMap<String, MutableList<Int>>
