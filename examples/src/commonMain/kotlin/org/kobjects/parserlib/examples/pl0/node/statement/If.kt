package org.kobjects.parserlib.examples.pl0.node.statement

import org.kobjects.parserlib.examples.pl0.EvaluationContext
import org.kobjects.parserlib.examples.pl0.node.condition.Condition

data class If(val condition: Condition, val statement: Statement) : Statement() {
    override fun eval(context: EvaluationContext) {
        if (condition.eval(context)) {
            statement.eval(context)
        }
    }
    override fun toString(indent: String) = "IF $condition THEN ${statement.toString(indent)}"
}