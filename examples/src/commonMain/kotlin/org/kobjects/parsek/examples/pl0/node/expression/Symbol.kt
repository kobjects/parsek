package org.kobjects.parsek.examples.pl0.node.expression

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext

data class Symbol(
    val name: String
) : Expression {
    override fun eval(context: EvaluationContext) = context.get(name)
    override fun toString() = name
}