package org.kobjects.parserlib.examples.pl0.node.condition

import org.kobjects.parserlib.examples.pl0.EvaluationContext
import org.kobjects.parserlib.examples.pl0.node.expression.Expression

data class Odd(val experession: Expression) : Condition {
    override fun eval(context: EvaluationContext): Boolean = (experession.eval(context) % 2) != 0
}