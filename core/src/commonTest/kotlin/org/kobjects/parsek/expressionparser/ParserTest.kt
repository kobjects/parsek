package org.kobjects.parsek.expressionparser

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

    object Calculator : ConfigurableExpressionParser<Scanner<TokenType>, Unit, Double>(
        { scanner, _ -> scanner.consume(TokenType.NUMBER).text.toDouble() },
        infix(3, "^") { _, _, _, left, right -> left.pow(right) },
        prefix(2, "+") { _, _, _, operand -> operand },
        prefix(2, "-") { _, _, _, operand -> -operand },
        infix(1, "*") { _, _, _, left, right -> left * right },
        infix(1, "/") { _, _, _, left, right -> left / right },
        infix(0, "+") { _, _, _, left, right -> left + right },
        infix(0, "-") { _, _, _, left, right -> left - right },
    ) {
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