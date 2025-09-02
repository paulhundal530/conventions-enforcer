package conventions.enforcer.impl.violations

import conventions.enforcer.api.Violation
import java.nio.file.Path

data class AppliedPluginViolation(
    override val message: String,
    override val buildFile: Path,
    val position: Int
) : Violation
