package org.kobjects.parserlib.expressionparser

import org.kobjects.expressionparser.ExpressionParser
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Tokenizer
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    enum class TokenType {
        BOF, NUMBER, IDENTIFIER, SYMBOL, EOF
    }

    fun createTokenizer(input: String): Tokenizer<TokenType?> {
        return Tokenizer(
            TokenType.BOF,
            listOf(
                RegularExpressions.WHITESPACE to null,
                RegularExpressions.SYMBOL to TokenType.SYMBOL,
                RegularExpressions.IDENTIFIER to TokenType.IDENTIFIER,
                RegularExpressions.NUMBER to TokenType.NUMBER,
                ),
            TokenType.EOF,
            input)

    }

    val parser = ExpressionParser<TokenType?, Double> { parser, tokenizer ->
        when (tokenizer.current.type) {
            TokenType.NUMBER -> {
                val result = tokenizer.current.value.toDouble()
                tokenizer.next()
                result
            }
            TokenType.SYMBOL -> if (tokenizer.current.value == "(") {
                val result = parser.parse(tokenizer)
                tokenizer.consume(")")
                result
            } else {
                throw tokenizer.error("Number or group expected.")
            }
            else -> throw tokenizer.error("Number or group expected.")
        }
    }.apply {
        addInfix(2, "*") { _, _, left, right -> left * right }
        addInfix(2, "/") { _, _, left, right -> left / right }
        addInfix(1, "+") { _, _, left, right -> left + right }
        addInfix(1, "-") { _, _, left, right -> left - right }
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
}