package org.kobjects.parserlib.examples.pl0.node.expression

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext

data class Negate(val operand: Expression) : Expression {
    override fun eval(context: EvaluationContext) = -operand.eval(context)
    override fun toString() = "-$operand"
}