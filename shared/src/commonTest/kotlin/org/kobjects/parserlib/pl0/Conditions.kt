package org.kobjects.parserlib.pl0

interface Condition {
    fun eval(context: EvaluationContext): Boolean
}

data class Odd(val experession: Expression) : Condition {
    override fun eval(context: EvaluationContext): Boolean = (experession.eval(context) % 2) != 0
}

data class Comparison(
    val name: String,
    val left: Expression,
    val right: Expression,
    val implementation: (Int, Int) -> Boolean
) : Condition {
    override fun eval(context: EvaluationContext) =
        implementation(left.eval(context), right.eval(context))
    override fun toString() = "$left $name $right"
}

