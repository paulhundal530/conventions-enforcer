package conventions.enforcer.util

import conventions.enforcer.api.Violation
import java.io.File

class ReportPublisher {
    fun publish(violations: Set<Violation>, output: File) {
        output.printWriter().use { writer ->
            writer.println("<!DOCTYPE html>")
            writer.println("<html><head><meta charset='UTF-8'>")
            writer.println("<title>Conventions Enforcer Report</title>")
            writer.println("<style>")
            writer.println("body { font-family: sans-serif; padding: 20px; }")
            writer.println("h1 { color: #333; }")
            writer.println("h2 { color: #c00; margin-top: 30px; }")
            writer.println("ul { list-style-type: none; padding-left: 0; }")
            writer.println("li { margin-bottom: 10px; background: #f9f9f9; padding: 10px; border-left: 4px solid #c00; }")
            writer.println("code { font-family: monospace; background: #eee; padding: 2px 4px; }")
            writer.println("</style></head><body>")

            writer.println("<h1>Conventions Enforcer Report</h1>")
            writer.println("<p><strong>Total Violations:</strong> ${violations.size}</p>")

            val grouped = violations.groupBy { it::class.simpleName ?: "UnknownRule" }

            grouped.forEach { (rule, group) ->
                val cleanRuleName = rule
                    .removeSuffix("Violation")
                    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
                    .replaceFirstChar { it.uppercase() }

                writer.println("<h2>❌ Rule Violation: $cleanRuleName</h2>")
                writer.println("<ul>")
                group.forEach { v ->
                    val line = Regex(""".*:(\d+)""").find(v.message)?.groupValues?.get(1) ?: "?"
                    writer.println("<li><code>${v.buildFile}:$line</code><br>${v.message.substringAfter(": ").trim()}</li>")
                }
                writer.println("</ul>")
            }

            writer.println("</body></html>")
        }
    }
}
