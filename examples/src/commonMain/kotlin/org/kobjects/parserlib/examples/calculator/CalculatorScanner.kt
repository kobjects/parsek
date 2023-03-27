package org.kobjects.parserlib.examples.calculator

import org.kobjects.parserlib.tokenizer.Lexer
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Scanner

class CalculatorScanner(input: String) : Scanner<TokenType>(
    Lexer(
        input,
        RegularExpressions.WHITESPACE to { null },
        RegularExpressions.NUMBER to { TokenType.NUMBER },
        Regex("\\+|-|\\*|\\/") to { TokenType.OPERATOR },
    ),
    TokenType.EOF)