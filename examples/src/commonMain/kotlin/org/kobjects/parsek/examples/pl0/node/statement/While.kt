package org.kobjects.parsek.examples.pl0.node.statement

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext
import org.kobjects.parsek.examples.pl0.node.condition.Condition

data class While(val condition: Condition, val statement: Statement) : Statement() {
    override fun eval(context: EvaluationContext) {
        while (condition.eval(context)) {
            statement.eval(context)
        }
    }
    override fun toString(indent: String) = "WHILE $condition DO ${statement.toString(indent)}"
}