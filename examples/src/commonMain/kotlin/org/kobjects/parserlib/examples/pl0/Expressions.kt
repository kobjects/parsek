package org.kobjects.parserlib.examples.pl0

interface Expression {
    fun eval(context: EvaluationContext): Int
}

data class UnaryExpression(
    val name: String,
    val operand: Expression,
    val implementation: (Int) -> Int
) : Expression {
    override fun eval(context: EvaluationContext) = implementation(operand.eval(context))
    override fun toString() = "$name $operand"
}

data class BinaryExpression(
    val operation: String,
    val left: Expression,
    val right: Expression,
    val implementation: (Int, Int) -> Int) :
    Expression {
    override fun eval(context: EvaluationContext) = implementation(left.eval(context), right.eval(context))
    override fun toString() = "($left $operation $right)"
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