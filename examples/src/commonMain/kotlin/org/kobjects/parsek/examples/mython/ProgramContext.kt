package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.Literal
import org.kobjects.parsek.examples.expressions.RootContext
import org.kobjects.parsek.examples.expressions.RuntimeContext

class ProgramContext(
    val program: Program,
    val printFn: (String) -> Unit
) : RuntimeContext {

    override fun evalSymbol(name: String, children: List<Evaluable>, parameterContext: RuntimeContext): Any =
        program.functions[name]?.eval(children, parameterContext) ?: when (name) {
            "for" -> {
                val range = children[1].eval(parameterContext) as Collection<Any>
                for (value in range) {
                    val loopContext = LocalContext(parameterContext)
                    loopContext.symbols[(children[0] as Literal).value as String] = value
                    children[2].eval(loopContext)
                }
            }
            "if" -> evalIf(children, parameterContext)
            "print" -> printFn(children.joinToString { it.eval(parameterContext).toString() })
            "range" -> when (children.size) {
                1 -> LongRange(0, children[0].evalLong(parameterContext) - 1)
                2 -> LongRange(
                    children.first().evalLong(parameterContext),
                    children.last().evalLong(parameterContext) - 1
                )
                else -> throw IllegalArgumentException("2 or 3 parameter expected for range, but got ${children.size}")
            }.map { it.toDouble() }
            "seq" -> children.fold(Unit) { _, current -> current.eval(parameterContext) }
            "=" -> {
                require(children.size == 2) { "Two parameters expected for assignment"}
                val target = (children.first() as Literal).value as String
                (parameterContext as LocalContext).symbols[target] = children.last().eval(parameterContext)
            }
            "while" -> {
                require(children.size == 2) { "Two parameters expected for 'while'."}
                while (children[0].evalBoolean(parameterContext)) children[1].eval(parameterContext)
            }
            else -> RootContext.evalSymbol(name, children, parameterContext)
        }

    fun evalIf(children: List<Evaluable>, parameterContext: RuntimeContext): Any {
        var i = 0
        while (i < children.size - 1) {
            if (children[i].evalBoolean(parameterContext)) {
                return children[i + 1].eval(parameterContext)
            }
            i += 2
        }
        return if (i < children.size) children.last().eval(parameterContext) else Unit
    }
}