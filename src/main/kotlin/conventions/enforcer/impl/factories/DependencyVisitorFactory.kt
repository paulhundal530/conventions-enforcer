package conventions.enforcer.impl.factories

import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.rules.BannedDependencyRule
import conventions.enforcer.impl.visitors.DependenciesVisitor

class DependencyVisitorFactory(
    private val visitorManager: VisitorManager,
    private val bannedDependencies: Set<String>,
) : VisitorFactory<DependenciesVisitor> {
    val rules = listOf(
        BannedDependencyRule(
            visitorManager = visitorManager,
            banned = bannedDependencies
        )
    )
    override fun create(): DependenciesVisitor =
        DependenciesVisitor(
            dependencyRules = rules,
            visitorManager = visitorManager,
        )
}
