package org.kobjects.parserlib.examples.pl0

interface Condition {
    fun eval(context: EvaluationContext): Boolean
}

data class Odd(val experession: Expression) : Condition {
    override fun eval(context: EvaluationContext): Boolean = (experession.eval(context) % 2) != 0
}

data class RelationalOperation(
    val name: String,
    val leftChild: Expression,
    val rightChild: Expression
) : Condition {
    override fun eval(context: EvaluationContext): Boolean {
        val leftValue = leftChild.eval(context)
        val rightValue = rightChild.eval(context)
        return when (name) {
            "=" -> leftValue == rightValue
            "#" -> leftValue != rightValue
            "<" -> leftValue < rightValue
            "<=" -> leftValue <= rightValue
            ">" -> leftValue > rightValue
            ">=" -> leftValue >= rightValue
            else -> throw UnsupportedOperationException(name)
        }
    }
    override fun toString() = "$leftChild $name $rightChild"
}
