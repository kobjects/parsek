package org.kobjects.parserlib.examples.expressions

import org.kobjects.parserlib.tokenizer.Lexer
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Scanner

class Tokenizer(input: String) : Scanner<TokenType>(
    Lexer(
        input,
        RegularExpressions.WHITESPACE to { null },
        RegularExpressions.NUMBER to { TokenType.NUMBER },
        RegularExpressions.DOUBLE_QUOTED_STRING to { TokenType.STRING },
        RegularExpressions.IDENTIFIER to { TokenType.IDENTIFIER },
        RegularExpressions.SYMBOL to { TokenType.SYMBOL },
        ),
    TokenType.EOF)