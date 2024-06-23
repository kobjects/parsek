package org.kobjects.parsek.examples.calculator

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner

class Tokenizer(input: String) : Scanner<TokenType>(
    Lexer(
        input,
        RegularExpressions.WHITESPACE to { null },
        RegularExpressions.NUMBER to { TokenType.NUMBER },
        Regex("\\+|-|\\*|\\/|\\^") to { TokenType.OPERATOR },
    ),
    TokenType.EOF) {

}