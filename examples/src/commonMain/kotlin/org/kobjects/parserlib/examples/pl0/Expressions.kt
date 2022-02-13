package org.kobjects.parserlib.examples.pl0

interface Expression {
    fun eval(context: EvaluationContext): Int
}

data class Negate(val operand: Expression) : Expression {
    override fun eval(context: EvaluationContext) = -operand.eval(context)
    override fun toString() = "-$operand"
}

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

data class Number(
    val value: Int
) : Expression {
    override fun eval(context: EvaluationContext) = value
    override fun toString() = value.toString()
}

data class Symbol(
    val name: String
) : Expression {
    override fun eval(context: EvaluationContext) = context.get(name)
    override fun toString() = name
}