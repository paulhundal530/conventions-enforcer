package conventions.enforcer.impl

import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.factories.AllowedClosureVisitorFactory
import conventions.enforcer.impl.factories.ApplyPluginVisitorFactory
import conventions.enforcer.impl.factories.DependencyVisitorFactory

data class VisitationInput(
    val allowlistClosures: Set<String>,
    val allowlistPlugins: Set<String>,
    val bannedDependencies: Set<String>,
    val visitorManager: VisitorManager,
)

object VisitorWarehouse {
    fun create(
        input: VisitationInput
    ): List<Visitor> {
        val visitors: MutableList<Visitor> = mutableListOf()
        visitors.apply {
            add(AllowedClosureVisitorFactory(input.visitorManager, input.allowlistClosures).create())
            add(ApplyPluginVisitorFactory(input.visitorManager, input.allowlistPlugins).create())
            add(DependencyVisitorFactory(input.visitorManager, input.bannedDependencies).create())
        }
        return visitors
    }
}