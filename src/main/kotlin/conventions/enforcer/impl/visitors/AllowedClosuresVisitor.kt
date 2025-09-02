package conventions.enforcer.impl.visitors

import conventions.enforcer.api.ClosureRule
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression

/**
 * Visitor that inspects method call closures and records only those
 * not in the allowed list (e.g., disallowed plugin blocks).
 */
class AllowedClosuresVisitor(
    closureRules: List<ClosureRule>,
    visitorManager: VisitorManager,
    private val allowList: Set<String>
) : Visitor(closureRules, visitorManager) {

    private val methodCalls: MutableMap<Int, String> = mutableMapOf()
    private val ignoredLines: MutableSet<Int> = mutableSetOf()

    private var currentStart: Int = -1
    private var currentEnd: Int = -1

    override fun visitMethodCallExpression(call: MethodCallExpression) {
        val methodName = call.methodAsString ?: return

        // Ignore allowed closures by line range
        if (methodName in allowList) {
            currentStart = call.lineNumber
            currentEnd = call.lastLineNumber
            for (line in (currentStart + 1)..currentEnd) {
                ignoredLines.add(line)
            }
        }

        methodCalls[call.lineNumber] = methodName
        super.visitMethodCallExpression(call)
    }

    override fun visitClosureExpression(expression: ClosureExpression) {
        val lineNumber = expression.lineNumber
        val name = methodCalls[lineNumber]

        if (name != null && name !in allowList) {
            add(name, lineNumber)
        }

        super.visitClosureExpression(expression)
    }

    override fun clear() {
        methodCalls.clear()
        ignoredLines.clear()
        currentStart = -1
        currentEnd = -1
    }
}
