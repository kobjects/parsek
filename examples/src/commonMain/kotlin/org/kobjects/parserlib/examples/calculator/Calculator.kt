package org.kobjects.parserlib.examples.calculator

import org.kobjects.parserlib.expressionparser.ExpressionParser

object Calculator : ExpressionParser<CalculatorScanner, Unit, Double>(
    ExpressionParser.prefix(0, "+") { _, _, _, operand -> operand },
    ExpressionParser.prefix(0, "-") { _, _, _, operand -> -operand },
    ExpressionParser.infix(1, "*") { _, _, _, left, right -> left * right },
    ExpressionParser.infix(1, "/") { _, _, _, left, right -> left / right },
    ExpressionParser.infix(2, "+") { _, _, _, left, right -> left + right },
    ExpressionParser.infix(2, "-") { _, _, _, left, right -> left - right },
    parsePrimary = { scanner, _ -> scanner.consume(TokenType.NUMBER).text.toDouble() },
) {


    fun eval(expression: String): Double = Calculator.parse(CalculatorScanner(expression), Unit)


}