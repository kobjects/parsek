package org.kobjects.parserlib.tokenizer

import kotlin.test.*

class TokenizerTest {

    enum class TokenType {
        BOF, NUMBER, IDENTIFIER, SYMBOL, EOF
    }

    @Test
    fun testSimple() {
        val tokenizer = createTokenizer("4 + x")

        assertEquals(TokenType.BOF, tokenizer.current.type)
        assertEquals(TokenType.BOF, tokenizer.next().type)

        assertFalse(tokenizer.eof)
        assertTypeAndValue(TokenType.NUMBER, "4", tokenizer.current)
        assertTypeAndValue(TokenType.NUMBER, "4", tokenizer.next())

        assertTypeAndValue(TokenType.SYMBOL, "+", tokenizer.current)
        assertTypeAndValue(TokenType.SYMBOL, "+", tokenizer.next())

        assertTypeAndValue(TokenType.IDENTIFIER, "x", tokenizer.next())

        assertEquals(TokenType.EOF, tokenizer.next().type)
        assertTrue(tokenizer.eof)
    }


    companion object {

        fun createTokenizer(input: String): Tokenizer<TokenType?> {
            return Tokenizer(
                input,
                TokenType.BOF,
                TokenType.EOF,
                RegularExpressions.WHITESPACE to null,  // Don't report
                RegularExpressions.SYMBOL to TokenType.SYMBOL,
                RegularExpressions.IDENTIFIER to TokenType.IDENTIFIER,
                RegularExpressions.NUMBER to TokenType.NUMBER,
        ) }

        fun assertTypeAndValue(type: TokenType, value: String, token: Token<TokenType?>) {
            assertEquals(type, token.type)
            assertEquals(value, token.text)
        }


    }

}