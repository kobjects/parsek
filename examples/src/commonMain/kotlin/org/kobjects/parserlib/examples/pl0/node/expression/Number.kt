package org.kobjects.parserlib.examples.pl0.node.expression

import org.kobjects.parserlib.examples.pl0.EvaluationContext

data class Number(
    val value: Int
) : Expression {
    override fun eval(context: EvaluationContext) = value
    override fun toString() = value.toString()
}