package conventions

import conventions.enforcer.api.VisitedNodes
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager

class FakeVisitorManager : VisitorManager {
    private val data: MutableMap<Class<out Visitor>, VisitedNodes> = mutableMapOf()

    override val visitedNotes: Map<Class<out Visitor>, VisitedNodes>
        get() = data

    override fun add(visitor: Class<out Visitor>, key: String, value: Int) {
        data.getOrPut(visitor) { mutableMapOf() }
            .getOrPut(key) { mutableListOf() }
            .add(value)
    }

    override fun clear() {
        data.clear()
    }
}