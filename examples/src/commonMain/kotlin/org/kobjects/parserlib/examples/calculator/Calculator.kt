package org.kobjects.parserlib.examples.calculator

import org.kobjects.parserlib.expressionparser.ConfigurableExpressionParser

object Calculator : ConfigurableExpressionParser<Tokenizer, Unit, Double>(
    { scanner, _ -> scanner.consume(Tokenizer.TokenType.NUMBER).text.toDouble() },
    ConfigurableExpressionParser.prefix(2, "+") { _, _, _, operand -> operand },
    ConfigurableExpressionParser.prefix(2, "-") { _, _, _, operand -> -operand },
    ConfigurableExpressionParser.infix(1, "*") { _, _, _, left, right -> left * right },
    ConfigurableExpressionParser.infix(1, "/") { _, _, _, left, right -> left / right },
    ConfigurableExpressionParser.infix(0, "+") { _, _, _, left, right -> left + right },
    ConfigurableExpressionParser.infix(0, "-") { _, _, _, left, right -> left - right },
) {


    fun eval(expression: String): Double = Calculator.parseExpression(Tokenizer(expression), Unit)

}

