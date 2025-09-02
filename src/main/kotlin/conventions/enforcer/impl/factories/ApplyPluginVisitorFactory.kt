package conventions.enforcer.impl.factories

import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.rules.UnsupportedPluginRule
import conventions.enforcer.impl.visitors.ApplyPluginVisitor

internal class ApplyPluginVisitorFactory(
    private val visitorManager: VisitorManager,
    private val allowlistPlugins: Set<String>
) : VisitorFactory<ApplyPluginVisitor> {
    val rules = listOf(
        UnsupportedPluginRule(
            visitorManager = visitorManager,
            allowedPlugins = allowlistPlugins
        )
    )

    override fun create(): ApplyPluginVisitor =
        ApplyPluginVisitor(
            appliedPluginRules = rules,
            visitorManager = visitorManager,
        )
}
