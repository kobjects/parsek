package org.kobjects.parserlib.tokenizer

import kotlin.test.*

class TokenizerTest {

    enum class TokenType {
        BOF, NUMBER, IDENTIFIER, SYMBOL, EOF
    }

    @Test
    fun testSimple() {
        val tokenizer = createTokenizer("4 + x")

        assertFalse(tokenizer.eof)
        assertTypeAndValue(TokenType.NUMBER, "4", tokenizer.current)
        assertEquals("4", tokenizer.consume())

        assertTypeAndValue(TokenType.SYMBOL, "+", tokenizer.current)
        assertEquals( "+", tokenizer.consume())

        assertEquals( "x", tokenizer.consume())

        assertEquals(TokenType.EOF, tokenizer.current.type)
        assertTrue(tokenizer.eof)
    }


    companion object {

        fun createTokenizer(input: String): Tokenizer<TokenType?> {
            return Tokenizer(
                input,
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