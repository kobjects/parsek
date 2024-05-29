package org.kobjects.parsek.expressionparser


import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser.Companion.infix
import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    // A parser that evaluates the expression directly (opposed to building a tree).
    private val parser = ConfigurableExpressionParser<Scanner<TokenType>, Unit, Double>(
        { tokenizer, _ -> parsePrimary(tokenizer) },
        infix(2, "*") { _, _, _, left, right -> left * right },
        infix(2, "/") { _, _, _, left, right -> left / right },
        infix(1, "+") { _, _, _, left, right -> left + right },
        infix(1, "-") { _, _, _, left, right -> left - right }
    )

    fun parsePrimary(tokenizer: Scanner<TokenType>): Double =
        when (tokenizer.current.type) {
            TokenType.NUMBER -> tokenizer.consume().text.toDouble()
            TokenType.SYMBOL ->
                if (tokenizer.current.text == "(") {
                    val result = parser.parseExpression(tokenizer, Unit)
                    tokenizer.consume(")")
                    result
                } else {
                    throw tokenizer.exception("Number or group expected.")
                }
            else -> throw tokenizer.exception("Number or group expected.")
        }

    fun evaluate(expr: String): Double {
        val scanner = createScanner(expr)
        return parser.parseExpression(scanner, Unit)
    }

    @Test
    fun testParser() {
        assertEquals(-4.0, evaluate("4 - 4 - 4"))
        assertEquals(5.0, evaluate("3 + 2"))
        assertEquals(11.0, evaluate("3 + 2 * 4"))
    }

    companion object {
        enum class TokenType {
            NUMBER, IDENTIFIER, SYMBOL, EOF
        }

        fun createScanner(input: String): Scanner<TokenType> =
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