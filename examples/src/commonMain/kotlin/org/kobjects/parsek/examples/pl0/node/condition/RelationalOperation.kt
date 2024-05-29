package org.kobjects.parsek.examples.pl0.node.condition

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext
import org.kobjects.parsek.examples.pl0.node.expression.Expression

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
