package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.RootContext
import org.kobjects.parsek.examples.expressions.RuntimeContext

class ProgramContext(
    val program: Program,
    val printFn: (String) -> Unit
) : RuntimeContext {

    override fun evalSymbol(name: String, children: List<Evaluable>, parameterContext: RuntimeContext): Any =
        program.functions[name]?.eval(children, parameterContext) ?: when (name) {
            "sequence" -> children.fold(Unit) { acc, current -> current.eval(parameterContext) }
            "if" -> if (children[0].evalBoolean(parameterContext)) children[1].eval(parameterContext) else Unit
            "while" -> while (children[0].evalBoolean(parameterContext)) children[1].eval(parameterContext)
            else -> RootContext.evalSymbol(name, children, parameterContext)
        }

}