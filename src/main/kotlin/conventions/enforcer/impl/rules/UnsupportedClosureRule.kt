package conventions.enforcer.impl.rules

import conventions.enforcer.api.ClosureRule
import conventions.enforcer.api.Violation
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import conventions.enforcer.impl.violations.ClosureViolation
import java.nio.file.Path
import java.util.TreeSet

/**
 * A rule that enforces only explicitly allowed closures are used in build scripts.
 *
 * This rule scans all closure nodes visited by a [Visitor] and flags any usage of
 * closures whose names are not included in the provided allowlist.
 *
 * Example of allowed closure names: "android", "dependencies", "plugins"
 *
 * @param allowlist A set of closure names that are permitted.
 * @param visitorManager The central manager that tracks visited AST nodes and their positions.
 */
class UnsupportedClosureRule(
    private val allowlist: Set<String>,
    private val visitorManager: VisitorManager,
) : ClosureRule {

    /**
     * Applies this rule to the given [buildFile] using the provided [visitor].
     *
     * @return A set of [Violation]s indicating disallowed closures found in the file.
     */
    override fun enforce(
        buildFile: Path,
        visitor: Visitor
    ): Set<Violation> {
        val broken = TreeSet<Violation>()
        val visited = visitorManager.visitedNotes[visitor.javaClass] ?: return emptySet()

        visited.forEach { (nodeName, positions) ->
            // Match closure names exactly unless pattern-based matching is intended
            if (nodeName !in allowlist) {
                positions.forEach { position ->
                    broken.add(
                        ClosureViolation(
                            message = "The closure '$nodeName' at $buildFile:$position is not supported. " +
                                    "Please use one of the allowed closures: ${allowlist.joinToString(", ")}",
                            buildFile = buildFile
                        )
                    )
                }
            }
        }

        return broken
    }
}