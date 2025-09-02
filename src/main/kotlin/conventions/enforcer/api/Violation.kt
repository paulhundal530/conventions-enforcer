package conventions.enforcer.api

import java.nio.file.Path
import kotlin.io.path.name

/**
 * Represents a single violation of a defined convention rule.
 *
 * Each violation is associated with a specific build file and includes a human-readable
 * message explaining the nature of the violation.
 *
 * Violations are comparable to each other. Sorting is done first by the file name,
 * and then by the message content to ensure stable output ordering.
 */
interface Violation : Comparable<Violation> {
    /**
     * A human-readable description of what rule was violated.
     */
    val message: String
    /**
     * The absolute path to the build file where the violation occurred.
     */
    val buildFile: Path

    override fun compareTo(other: Violation): Int {
        val comparison = this.buildFile.name.compareTo(other.buildFile.name)
        return if (comparison != 0) {
            comparison
        } else {
            this.message.compareTo(other.message)
        }
    }
}