package org.kobjects.parsek.examples.pl0.node.statement

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext

data class BeginEnd(
    val statements: List<Statement>
) : Statement() {
    override fun eval(context: EvaluationContext) {
        statements.forEach { it.eval(context) }
    }
    override fun toString(indent: String): String {
        val sb = StringBuilder("\n")
        sb.append(indent).append("BEGIN\n")
        for (i in statements.indices) {
            sb.append(indent).append("  ")
            sb.append(statements[i].toString("  " + indent))
            if (i < statements.size - 1) {
                sb.append(';')
            }
            sb.append('\n')
        }
        sb.append(indent).append("END")
        return sb.toString()
    }
}