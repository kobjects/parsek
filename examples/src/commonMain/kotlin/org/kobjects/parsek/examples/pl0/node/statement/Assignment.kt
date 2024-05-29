package org.kobjects.parsek.examples.pl0.node.statement

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext
import org.kobjects.parsek.examples.pl0.node.expression.Expression

data class Assignment(
    val variable: String,
    val expression: Expression
) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.set(variable, expression.eval(context))
    }
    override fun toString(indent: String) = "$variable := $expression"
}