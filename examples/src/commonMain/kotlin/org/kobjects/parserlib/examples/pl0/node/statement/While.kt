package org.kobjects.parserlib.examples.pl0.node.statement

import org.kobjects.parserlib.examples.pl0.EvaluationContext
import org.kobjects.parserlib.examples.pl0.node.condition.Condition

data class While(val condition: Condition, val statement: Statement) : Statement() {
    override fun eval(context: EvaluationContext) {
        while (condition.eval(context)) {
            statement.eval(context)
        }
    }
    override fun toString(indent: String) = "WHILE $condition DO ${statement.toString(indent)}"
}