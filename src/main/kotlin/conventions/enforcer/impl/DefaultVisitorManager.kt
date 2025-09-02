package conventions.enforcer.impl

import conventions.enforcer.api.VisitedNodes
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager

/**
 * Default in-memory implementation of [VisitorManager].
 *
 * Tracks visited nodes per visitor class and provides methods for recording and resetting state.
 */
internal class DefaultVisitorManager : VisitorManager {

    private val _visitedNodes: MutableMap<Class<out Visitor>, VisitedNodes> = mutableMapOf()

    /**
     * Read-only view of all visited nodes by visitor type.
     */
    override val visitedNotes: Map<Class<out Visitor>, VisitedNodes>
        get() = _visitedNodes

    /**
     * Adds a visited node for the given visitor type.
     *
     * @param visitor The visitor class reporting the node
     * @param key     A logical identifier for the visited node (e.g., plugin id)
     * @param value   The position or line number the node appeared at
     */
    override fun add(visitor: Class<out Visitor>, key: String, value: Int) {
        _visitedNodes
            .getOrPut(visitor) { mutableMapOf() }
            .getOrPut(key) { mutableListOf() }
            .add(value)
    }

    /**
     * Clears all visited node state.
     */
    override fun clear() {
        _visitedNodes.clear()
    }
}