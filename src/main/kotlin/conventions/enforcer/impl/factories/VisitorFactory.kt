package conventions.enforcer.impl.factories

import conventions.enforcer.api.Visitor

interface VisitorFactory<T: Visitor> {
    fun create(): T
}
