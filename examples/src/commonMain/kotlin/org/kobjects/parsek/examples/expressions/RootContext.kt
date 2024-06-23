package org.kobjects.parsek.examples.expressions

import kotlin.math.pow

object RootContext : RuntimeContext {


    override fun evalSymbol(name: String, children: List<Node>): Any =
        when (name) {
            "add" -> children.fold(0.0) { acc, current -> acc + current.evalDouble(this) }
            "mul" -> children.fold(1.0) { acc, current -> acc * current.evalDouble(this) }
            "div" -> numeric2(children) { a, b -> a / b }
            "sub" -> numeric2(children) { a, b -> a - b }
            "pow" -> numeric2(children) { a, b -> a.pow(b) }
            else -> throw IllegalStateException("Unrecognized symbol: $name")
        }
}