package org.kobjects.parsek.expression

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    @Test
    fun testParser() {
        assertEquals(-4.0, Calculator.calculate("4 - 4 - 4"))
        assertEquals(5.0, Calculator.calculate("3 + 2"))
        assertEquals(11.0, Calculator.calculate("3 + 2 * 4"))
    }

    enum class TokenType {
        NUMBER, IDENTIFIER, SYMBOL, EOF
    }

    object Calculator : PrattParser<Scanner<TokenType>, Unit, Double>(
        { scanner, _ -> scanner.consume(TokenType.NUMBER).text.toDouble() },
        { _, _, name, operand -> Calculator.evalUnary(name, operand ) },
        { _, _, name, left, right -> Calculator.evalBinary(name, left, right) },
        Operator.Infix(3, "^"),
        Operator.Prefix(2, "+", "-"),
        Operator.Infix(1, "*", "/"),
        Operator.Infix(0, "+", "-"),
    ) {
        fun evalUnary(name: String, operand: Double) = when (name) {
            "-" -> -operand
            "+" -> operand
            else -> throw IllegalArgumentException("Unrecognized unary operator: $name")
        }

        fun evalBinary(name: String, left: Double, right: Double) = when (name) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            "^" -> left.pow(right)
            else -> throw IllegalArgumentException("Unrecognized binary operator: $name")
        }

        fun calculate(expression: String): Double = Calculator.parseExpression(createScanner(expression), Unit)

        private fun createScanner(input: String): Scanner<TokenType> =
            Scanner(
                Lexer(
                    input,
                    RegularExpressions.WHITESPACE to { null },
                    RegularExpressions.SYMBOL to { TokenType.SYMBOL },
                    RegularExpressions.IDENTIFIER to { TokenType.IDENTIFIER },
                    RegularExpressions.NUMBER to { TokenType.NUMBER },
                ),
                TokenType.EOF)
    }
}