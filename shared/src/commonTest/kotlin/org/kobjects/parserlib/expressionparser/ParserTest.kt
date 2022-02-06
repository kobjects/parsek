package org.kobjects.parserlib.expressionparser


import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Tokenizer
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    // A parser that evaluates the expression directly (opposed to building a tree).
    private val parser = object : ExpressionParser<TokenType, Double>(
        infix(2, "*") { _, _, left, right -> left * right },
        infix(2, "/") { _, _, left, right -> left / right },
        infix(1, "+") { _, _, left, right -> left + right },
        infix(1, "-") { _, _, left, right -> left - right },
    ) {
        override fun parsePrimary(tokenizer: Tokenizer<TokenType>) =
            when (tokenizer.current.type) {
                TokenType.NUMBER -> {
                    val result = tokenizer.current.value.toDouble()
                    tokenizer.next()
                    result
                }
                TokenType.SYMBOL -> if (tokenizer.current.value == "(") {
                    val result = parse(tokenizer)
                    tokenizer.consume(")")
                    result
                } else {
                    throw tokenizer.error("Number or group expected.")
                }
                else -> throw tokenizer.error("Number or group expected.")
        }
    }

    fun evaluate(expr: String): Double {
        val tokenizer = createTokenizer(expr)
        tokenizer.next()
        return parser.parse(tokenizer)
    }

    @Test
    fun testParser() {
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
            Tokenizer(TokenType.BOF, TOKEN_LIST, TokenType.EOF, input)
    }


}