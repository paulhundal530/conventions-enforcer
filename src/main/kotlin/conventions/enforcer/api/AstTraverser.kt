package conventions.enforcer.api

import conventions.enforcer.impl.DefaultAstTraverser
import org.codehaus.groovy.ast.builder.AstBuilder
import org.gradle.api.logging.Logger
import java.nio.file.Path

/**
 * Responsible for traversing a list of Gradle build files and applying all rule-based visitors.
 */
interface AstTraverser {

    /**
     * Build files that were skipped due to Groovy parsing issues or irrecoverable errors.
     */
    val overlooked: Set<Path>

    /**
     * Traverse the provided list of build files and return all detected violations.
     *
     * @param buildFiles Paths to Gradle build scripts (.gradle or .gradle.kts)
     * @return A set of violations detected across all traversed files.
     */
    fun traverse(buildFiles: List<Path>): Set<Violation>

    companion object {
        /**
         * Creates a default in-memory AST Traverser.
         */
        fun create(
            astBuilder: AstBuilder,
            visitors: List<Visitor>,
            visitorManager: VisitorManager,
            logger: Logger
        ): AstTraverser = DefaultAstTraverser(
            astBuilder = astBuilder,
            visitors = visitors,
            visitorManager = visitorManager,
            logger = logger
        )
    }
}