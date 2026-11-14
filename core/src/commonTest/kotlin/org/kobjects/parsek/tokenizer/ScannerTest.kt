package org.kobjects.parsek.scanner

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.parsek.tokenizer.Token
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
        assertEquals("4", scanner.consume().text)

        assertTypeAndValue(TokenType.SYMBOL, "+", scanner.current)
        assertEquals("+", scanner.consume().text)

        assertEquals("x", scanner.consume().text)

        assertEquals(null, scanner.current.type)
        assertEquals("<EOF>", scanner.current.text)
        assertTrue(scanner.eof)
    }

    @Test
    fun testEmpty() {
        val scanner = createScanner("")

        assertTrue(scanner.eof)
        assertEquals(null, scanner.current.type)
        assertEquals("<EOF>", scanner.current.text)
        assertEquals(0, scanner.current.pos)
        assertEquals(0, scanner.current.col)
    }

    @Test
    fun testIgnoreCase() {
        val scanner = createScanner("Print hello")

        assertFalse(scanner.tryConsume("print"))
        assertTrue(scanner.tryConsumeIgnoreCase("print"))
        assertEquals("hello", scanner.consumeIgnoreCase("HELLO").text)
        assertTrue(scanner.eof)
    }

    @Test
    fun testLexerIterator() {
        val tokens = Lexer(
            "4 + x",
            RegularExpressions.WHITESPACE to null,
            RegularExpressions.SYMBOL to TokenType.SYMBOL,
            RegularExpressions.IDENTIFIER to TokenType.IDENTIFIER,
            RegularExpressions.UNSIGNED_NUMBER to TokenType.NUMBER,
        ).asSequence().toList()

        assertEquals(listOf("4", "+", "x"), tokens.map { it.text })
    }

    companion object {
        fun createScanner(input: String) = Scanner<TokenType?>(
            input,
            null,
            RegularExpressions.WHITESPACE to null,  // Don't report
            RegularExpressions.SYMBOL to TokenType.SYMBOL,
            RegularExpressions.IDENTIFIER to TokenType.IDENTIFIER,
            RegularExpressions.UNSIGNED_NUMBER to TokenType.NUMBER,
        )

        fun assertTypeAndValue(type: TokenType, value: String, token: Token<TokenType?>) {
            assertEquals(type, token.type)
            assertEquals(value, token.text)
        }
    }
}
