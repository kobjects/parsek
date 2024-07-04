package org.kobjects.parsek.examples.expressions

import kotlin.math.pow

object RootContext : RuntimeContext {


    override fun evalSymbol(name: String, children: List<Evaluable>, parameterContext: RuntimeContext): Any =
        when (name) {
            "add" -> children.fold(0.0) { acc, current -> acc + current.evalDouble(parameterContext) }
            "mul" -> children.fold(1.0) { acc, current -> acc * current.evalDouble(parameterContext) }
            "div" -> parameterContext.numeric2(children) { a, b -> a / b }
            "sub" -> parameterContext.numeric2(children) { a, b -> a - b }
            "pow" -> parameterContext.numeric2(children) { a, b -> a.pow(b) }
            else -> throw IllegalStateException("Unrecognized symbol: $name")
        }
}