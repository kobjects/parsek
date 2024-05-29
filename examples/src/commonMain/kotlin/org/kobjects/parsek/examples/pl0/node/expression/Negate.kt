package org.kobjects.parsek.examples.pl0.node.expression

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext

data class Negate(val operand: Expression) : Expression {
    override fun eval(context: EvaluationContext) = -operand.eval(context)
    override fun toString() = "-$operand"
}