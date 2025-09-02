package conventions.enforcer.impl.visitors

import conventions.enforcer.api.AppliedPluginRule
import conventions.enforcer.api.Visitor
import conventions.enforcer.api.VisitorManager
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.TupleExpression

internal class ApplyPluginVisitor(
    appliedPluginRules: List<AppliedPluginRule>,
    visitorManager: VisitorManager,
) : Visitor(appliedPluginRules, visitorManager) {
    override fun visitMethodCallExpression(call: MethodCallExpression) {
        super.visitMethodCallExpression(call)
        when (call.methodAsString) {
            // Traditional (modern) plugin apply call
            "id" -> {
                val argValues = (call.arguments as TupleExpression).expressions.mapNotNull { arg ->
                    if (arg is ConstantExpression) arg.value as? String else null
                }
                val pluginId = argValues.joinToString(separator = ",")
                add(pluginId, call.lineNumber)
            }
            // is "apply ..." plugin application
            "apply" -> {
                val arguments = call.arguments as? TupleExpression ?: return
                arguments.expressions.mapNotNull { arg ->
                    if (arg is NamedArgumentListExpression) {
                        val arg0 = arg.mapEntryExpressions.firstOrNull() ?: return
                        val argName = (arg0.keyExpression as ConstantExpression).value
                        val argValue = arg0.valueExpression
                        // "apply from: ..." DSL (script plugin)
                        // "apply plugin: ..." DSL (old plugin apply mechanism)
                        if (argName == "from" || argName == "plugin") {
                            if (argValue is MethodCallExpression) {
                                val args = argValue.arguments as? ArgumentListExpression ?: return
                                val pluginFileArg = args.expressions.firstOrNull() ?: return

                                val pluginText = if (pluginFileArg is ConstantExpression) {
                                    pluginFileArg.value.toString()
                                } else {
                                    pluginFileArg.text
                                }

                                add(pluginText, call.lineNumber)
                            }
                            if (argValue is MethodCallExpression) {
                                // Script plugin is being referenced via something like "rootProject.file(...)"
                                val pluginFile = (argValue.arguments as ArgumentListExpression).expressions.first()
                                add(pluginFile.text, call.lineNumber)
                            } else if (argValue is ConstantExpression) {
                                // Plugin is referenced directly with a string file path
                                add(argValue.text, call.lineNumber)
                            } else if (argValue is ConstructorCallExpression) {
                                // Script plugin is being referenced via something like "new File(rootDir, ...)"
                                val pluginFile = (argValue.arguments as ArgumentListExpression).expressions[1]
                                add(pluginFile.text, call.lineNumber)
                            } else {
                                // Something else ???
                                add(argValue.text, call.lineNumber)
                            }
                        }
                    } else if (call.objectExpression.text.startsWith("plugin")) {
                        // Called apply() on plugins or pluginManager explicitly
                        add(arg.text, call.lineNumber)
                    }
                }
            }
        }
    }
}