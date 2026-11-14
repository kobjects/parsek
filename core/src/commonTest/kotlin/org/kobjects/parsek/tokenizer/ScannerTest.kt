package org.kobjects.parsek.scanner

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Token
import kotlin.test.*

class ScannerTest {

    enum class TokenType {
        NUMBER, IDENTIFIER, SYMBOL
    }

    @Test
    fun testSimple() {
        val scanner = lexer.scanner("4 + x")

        assertFalse(scanner.eof)
        assertTypeAndValue(TokenType.NUMBER, "4", scanner.current)
        assertEquals("4", scanner.consume().text)

        assertTypeAndValue(TokenType.SYMBOL, "+", scanner.current)
        assertEquals("+", scanner.consume().text)

        assertEquals("x", scanner.consume().text)

        assertEquals(null, scanner.current.type)
        assertTrue(scanner.eof)
    }


    companion object {
        val lexer = Lexer<TokenType?>(
            null,
            RegularExpressions.WHITESPACE to { null },  // Don't report
            RegularExpressions.SYMBOL to { TokenType.SYMBOL },
            RegularExpressions.IDENTIFIER to { TokenType.IDENTIFIER },
            RegularExpressions.UNSIGNED_NUMBER to { TokenType.NUMBER },
        )

        fun assertTypeAndValue(type: TokenType, value: String, token: Token<TokenType?>) {
            assertEquals(type, token.type)
            assertEquals(value, token.text)
        }
    }
}
