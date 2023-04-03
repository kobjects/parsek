package org.kobjects.parserlib.examples.calculator

import org.kobjects.parserlib.tokenizer.Lexer
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Scanner

class Tokenizer(input: String) : Scanner<Tokenizer.TokenType>(
    Lexer(
        input,
        RegularExpressions.WHITESPACE to { null },
        RegularExpressions.NUMBER to { TokenType.NUMBER },
        Regex("\\+|-|\\*|\\/") to { TokenType.OPERATOR },
    ),
    TokenType.EOF) {

    enum class TokenType {
        NUMBER, OPERATOR, EOF,
    }

}