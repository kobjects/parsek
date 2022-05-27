package org.kobjects.parserlib.expressionparser


import org.kobjects.parserlib.expressionparser.ExpressionParser.Companion.infix
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Tokenizer
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    // A parser that evaluates the expression directly (opposed to building a tree).
    private val parser = ExpressionParser<Tokenizer<TokenType>, Unit, Double>(
        infix(2, "*") { _, _, _, left, right -> left * right },
        infix(2, "/") { _, _, _, left, right -> left / right },
        infix(1, "+") { _, _, _, left, right -> left + right },
        infix(1, "-") { _, _, _, left, right -> left - right }
    ) { tokenizer, _ -> parsePrimary(tokenizer) }

    fun parsePrimary(tokenizer: Tokenizer<TokenType>): Double =
        when (tokenizer.current.type) {
            TokenType.NUMBER -> {
                val result = tokenizer.current.text.toDouble()
                tokenizer.next()
                result
            }
            TokenType.SYMBOL ->
                if (tokenizer.current.text == "(") {
                    val result = parser.parse(tokenizer, Unit)
                    tokenizer.consume(")")
                    result
                } else {
                    throw tokenizer.exception("Number or group expected.")
                }
            else -> throw tokenizer.exception("Number or group expected.")
        }

    fun evaluate(expr: String): Double {
        val tokenizer = createTokenizer(expr)
        tokenizer.next()
        return parser.parse(tokenizer, Unit)
    }

    @Test
    fun testParser() {
        assertEquals(-4.0, evaluate("4 - 4 - 4"))
        assertEquals(5.0, evaluate("3 + 2"))
        assertEquals(11.0, evaluate("3 + 2 * 4"))
    }

    companion object {
        enum class TokenType {
            BOF, NUMBER, IDENTIFIER, SYMBOL, EOF
        }

        private val TOKEN_LIST = listOf(
            RegularExpressions.WHITESPACE to null,
            RegularExpressions.SYMBOL to TokenType.SYMBOL,
            RegularExpressions.IDENTIFIER to TokenType.IDENTIFIER,
            RegularExpressions.NUMBER to TokenType.NUMBER,
        )

        fun createTokenizer(input: String): Tokenizer<TokenType> =
            Tokenizer(input, TokenType.BOF, TokenType.EOF, *TOKEN_LIST.toTypedArray())
    }


}