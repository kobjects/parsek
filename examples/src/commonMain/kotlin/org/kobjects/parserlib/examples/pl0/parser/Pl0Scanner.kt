package org.kobjects.parserlib.examples.pl0.parser

import org.kobjects.parserlib.tokenizer.Lexer
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Scanner

class Pl0Scanner(input: String) : Scanner<TokenType>(
    Lexer(
        input,
        RegularExpressions.WHITESPACE to { null },
        Regex("BEGIN|CALL|CONST|DO|END|IF|ODD|PROCEDURE|THEN|VAR|WHILE") to { TokenType.KEYWORD },
        Regex("[0-9]+") to { TokenType.NUMBER },
        Regex("[a-zA-Z]+") to {
            when (it) {
                "BEGIN", "CALL", "CONST", "DO", "END", "IF", "ODD", "PROCEDURE", "THEN", "VAR", "WHILE" -> TokenType.KEYWORD
                else -> TokenType.IDENT
            }
        },
        Regex("<=|>=|=|<|>|#") to { TokenType.COMPARISON },
        Regex("\\(|\\)|:=|;|\\.|!|\\?|\\+|-|\\*|/") to { TokenType.SYMBOL },
    ),
    TokenType.EOF,

)