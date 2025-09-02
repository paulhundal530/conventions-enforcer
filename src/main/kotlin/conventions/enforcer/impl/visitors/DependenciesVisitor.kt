package conventions.enforcer.impl.visitors

import conventions.enforcer.api.DependencyRule
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import org.codehaus.groovy.ast.expr.MethodCallExpression

class DependenciesVisitor(
    dependencyRules: List<DependencyRule>,
    private val visitorManager: VisitorManager
) : Visitor(dependencyRules, visitorManager) {

    private var insideDependenciesBlock = false

    override fun visitMethodCallExpression(call: MethodCallExpression) {
        val methodName = call.methodAsString ?: return

        if (methodName == "dependencies") {
            insideDependenciesBlock = true
        } else if (insideDependenciesBlock) {
            // Likely a dependency declaration like: implementation("group:artifact")
            val text = call.text.replace("this.", "")
            visitorManager.add(this.javaClass, text, call.lineNumber)
        }

        super.visitMethodCallExpression(call)
    }

    override fun clear() {
        insideDependenciesBlock = false
    }
}
