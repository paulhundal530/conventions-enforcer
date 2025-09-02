package conventions.enforcer.impl.violations

import conventions.enforcer.api.Violation
import java.nio.file.Path

internal data class ClosureViolation(
    override val message: String,
    override val buildFile: Path
) : Violation
