package org.kobjects.parserlib.scanner

import org.kobjects.parserlib.tokenizer.Lexer
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Scanner
import org.kobjects.parserlib.tokenizer.Token
import kotlin.test.*

class ScannerTest {

    enum class TokenType {
        NUMBER, IDENTIFIER, SYMBOL
    }

    @Test
    fun testSimple() {
        val scanner = createScanner("4 + x")

        assertFalse(scanner.eof)
        assertTypeAndValue(TokenType.NUMBER, "4", scanner.current)
        assertEquals("4", scanner.consume())

        assertTypeAndValue(TokenType.SYMBOL, "+", scanner.current)
        assertEquals( "+", scanner.consume())

        assertEquals( "x", scanner.consume())

        assertEquals(null, scanner.current.type)
        assertTrue(scanner.eof)
    }


    companion object {

        fun createScanner(input: String): Scanner<TokenType?> {
            return Scanner(
                Lexer(
                    input,
                    RegularExpressions.WHITESPACE to { null },  // Don't report
                    RegularExpressions.SYMBOL to { TokenType.SYMBOL },
                    RegularExpressions.IDENTIFIER to { TokenType.IDENTIFIER },
                    RegularExpressions.NUMBER to { TokenType.NUMBER }
                ),
                null)
        }

        fun assertTypeAndValue(type: TokenType, value: String, token: Token<TokenType?>) {
            assertEquals(type, token.type)
            assertEquals(value, token.text)
        }


    }

}