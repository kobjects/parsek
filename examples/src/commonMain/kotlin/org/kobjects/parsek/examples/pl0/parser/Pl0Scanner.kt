package org.kobjects.parsek.examples.pl0.parser

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner

class Pl0Scanner(input: String) : Scanner<TokenType>(
    Lexer(
        input,
        RegularExpressions.WHITESPACE to { null },
        Regex("BEGIN|CALL|CONST|DO|END|IF|ODD|PROCEDURE|THEN|VAR|WHILE") to { TokenType.KEYWORD },
        Regex("[0-9]+") to { TokenType.NUMBER },
        Regex("[a-zA-Z]+") to { TokenType.IDENT },
        Regex("<=|>=|=|<|>|#") to { TokenType.COMPARISON },
        Regex("\\(|\\)|:=|;|\\.|!|\\?|\\+|-|\\*|/") to { TokenType.SYMBOL },
    ),
    TokenType.EOF,

)