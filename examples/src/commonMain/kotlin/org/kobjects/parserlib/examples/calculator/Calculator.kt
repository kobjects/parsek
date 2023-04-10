package org.kobjects.parserlib.examples.calculator

import org.kobjects.parserlib.expressionparser.ConfigurableExpressionParser
import kotlin.math.pow

object Calculator : ConfigurableExpressionParser<Tokenizer, Unit, Double>(
    { scanner, _ -> scanner.consume(Tokenizer.TokenType.NUMBER).text.toDouble() },
    infix(3, "^") { _, _, _, left, right -> left.pow(right) },
    prefix(2, "+") { _, _, _, operand -> operand },
    prefix(2, "-") { _, _, _, operand -> -operand },
    infix(1, "*") { _, _, _, left, right -> left * right },
    infix(1, "/") { _, _, _, left, right -> left / right },
    infix(0, "+") { _, _, _, left, right -> left + right },
    infix(0, "-") { _, _, _, left, right -> left - right },
) {


    fun eval(expression: String): Double = Calculator.parseExpression(Tokenizer(expression), Unit)

}

