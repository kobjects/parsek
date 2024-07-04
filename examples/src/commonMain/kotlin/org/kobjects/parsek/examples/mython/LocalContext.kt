package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.RuntimeContext

class LocalContext(
    val parentContext: RuntimeContext
) : RuntimeContext {
    val symbols = mutableMapOf<String, Any>()

    override fun evalSymbol(name: String, children: List<Evaluable>, parameterContext: RuntimeContext): Any =
        when (val resolved = symbols[name]) {
            null -> parentContext.evalSymbol(name, children, parameterContext)
            is Lambda -> resolved.eval(children, parameterContext)
            else -> resolved
    }

}