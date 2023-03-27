package org.kobjects.parserlib.examples.pl0.node.expression

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext

data class BinaryOperation(
    val name: String,
    val leftOperand: Expression,
    val rightOperand: Expression
) : Expression {
    override fun eval(context: EvaluationContext): Int {
        val leftValue = leftOperand.eval(context)
        val rightValue = rightOperand.eval(context)
        return when (name) {
            "-" -> leftValue - rightValue
            "+" -> leftValue + rightValue
            "/" -> leftValue / rightValue
            "*" -> leftValue * rightValue
            else -> throw UnsupportedOperationException(name)
        }
    }
    // We need to insert parens to keep the precedence order as we don't keep them explicitly
    override fun toString() = "($leftOperand $name $rightOperand)"
}