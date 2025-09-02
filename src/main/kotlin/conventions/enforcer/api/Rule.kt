package conventions.enforcer.api

import java.nio.file.Path

/**
 * Represents a rule that can be enforced against a given Gradle build file.
 */
sealed interface Rule {

    /**
     * Evaluates the rule against the given build file using the provided visitor.
     *
     * @param buildFile The logical Gradle project path of the build file (e.g., ":app")
     * @param visitor   The visitor that can traverse the build script AST or structure.
     * @return A set of violations found for this rule in the build file.
     */
    fun enforce(buildFile: Path, visitor: Visitor): Set<Violation>
}

/**
 * Marker interface for rules that inspect closure blocks (e.g., custom DSLs, dependencies, etc.).
 */
interface ClosureRule : Rule

/**
 * Marker interface for rules that inspect declared dependencies.
 */
interface DependencyRule : Rule

/**
 * Market interface for rules that inspect applied plugins
 */
interface AppliedPluginRule : Rule