package conventions.enforcer.impl.rules

import conventions.enforcer.api.DependencyRule
import conventions.enforcer.api.Violation
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.violations.DependencyViolation
import java.nio.file.Path
import java.util.TreeSet

/**
 * A rule that enforces a ban on specific dependencies.
 *
 * It inspects the visited dependency declarations recorded by the visitor,
 * and flags any usage of dependencies that match entries in the banned list.
 *
 * @param banned List of banned dependencies, written as "group:artifact".
 * @param visitorManager Manager responsible for tracking visited AST nodes.
 */
class BannedDependencyRule(
    private val banned: Set<String>,
    private val visitorManager: VisitorManager
) : DependencyRule {

    override fun enforce(
        buildFile: Path,
        visitor: Visitor
    ): Set<Violation> {
        val violations = TreeSet<Violation>()

        val visited = visitorManager.visitedNotes[visitor.javaClass] ?: return emptySet()

        visited.forEach { (notation, positions) ->
            if (banned.any { bannedDep -> notation.contains(bannedDep) }) {
                positions.forEach { line ->
                    violations.add(
                        DependencyViolation(
                            message = "Banned dependency '$notation' used at $buildFile:$line. " +
                                    "This dependency is explicitly disallowed.",
                            buildFile = buildFile
                        )
                    )
                }
            }
        }

        return violations
    }
}