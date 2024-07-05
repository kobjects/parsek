package org.kobjects.parsek.examples.expressions

import kotlin.math.pow

object RootContext : RuntimeContext {


    override fun evalSymbol(name: String, children: List<Evaluable>, parameterContext: RuntimeContext): Any =
        when (name) {
            "+" -> children.fold(0.0) { acc, current -> acc + current.evalDouble(parameterContext) }
            "*" -> children.fold(1.0) { acc, current -> acc * current.evalDouble(parameterContext) }
            "/" -> parameterContext.numeric2(children) { a, b -> a / b }
            "%" -> parameterContext.numeric2(children) { a, b -> a % b }
            "-" -> if (children.size == 1) -children.first().evalDouble(parameterContext)
                else children.subList(1, children.size).fold(children.first().evalDouble(parameterContext)) { acc, current -> acc - current.evalDouble(parameterContext) }
            "**" -> parameterContext.numeric2(children) { a, b -> a.pow(b) }
            "==" -> children.first().eval(parameterContext) == children[1].eval(parameterContext)
            "=!" -> children.first().eval(parameterContext) != children[1].eval(parameterContext)
            else -> throw IllegalStateException("Unrecognized symbol: $name")
        }
}