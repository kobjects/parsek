package org.kobjects.parsek.examples.pl0.node.expression

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext

data class Number(
    val value: Int
) : Expression {
    override fun eval(context: EvaluationContext) = value
    override fun toString() = value.toString()
}