package conventions.enforcer.impl.rules

import conventions.enforcer.api.AppliedPluginRule
import conventions.enforcer.api.Violation
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.violations.AppliedPluginViolation
import java.nio.file.Path

class UnsupportedPluginRule(
    private val visitorManager: VisitorManager,
    private val allowedPlugins: Set<String>,
) : AppliedPluginRule {
    override fun enforce(
        buildFile: Path,
        visitor: Visitor
    ): Set<Violation> {
        val nodes = visitorManager.visitedNotes[visitor.javaClass] ?: return emptySet()

        return nodes
            .filter { it.key !in allowedPlugins }
            .flatMap { (node, positions) ->
                positions.map { position ->
                    AppliedPluginViolation(
                        "$buildFile:$position: \"$node\" is not an allowed plugin.",
                        buildFile,
                        position
                    )
                }
            }.toSortedSet()
    }
}