package org.kobjects.parsek.examples.pl0.node.condition

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext
import org.kobjects.parsek.examples.pl0.node.expression.Expression

data class Odd(val experession: Expression) : Condition {
    override fun eval(context: EvaluationContext): Boolean = (experession.eval(context) % 2) != 0
}