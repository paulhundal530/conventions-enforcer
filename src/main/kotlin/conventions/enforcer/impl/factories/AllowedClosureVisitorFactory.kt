package conventions.enforcer.impl.factories

import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.rules.UnsupportedClosureRule
import conventions.enforcer.impl.visitors.AllowedClosuresVisitor

class AllowedClosureVisitorFactory(
    private val visitorManager: VisitorManager,
    private val allowlistClosures: Set<String>
) : VisitorFactory<AllowedClosuresVisitor> {
    private val rules = listOf(
        UnsupportedClosureRule(
            allowlistClosures,
            visitorManager
        )
    )

    override fun create(): AllowedClosuresVisitor =
        AllowedClosuresVisitor(
            closureRules = rules,
            visitorManager = visitorManager,
            allowList = allowlistClosures
        )
}