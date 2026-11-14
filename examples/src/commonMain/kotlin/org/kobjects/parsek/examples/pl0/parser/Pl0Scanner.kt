package org.kobjects.parsek.examples.pl0.parser

import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner

private val KEYWORD_REGEX = Regex("BEGIN|CALL|CONST|DO|END|IF|ODD|PROCEDURE|THEN|VAR|WHILE")
private val NUMBER_REGEX = Regex("[0-9]+")
private val IDENT_REGEX = Regex("[a-zA-Z]+")
private val COMPARISON_REGEX = Regex("<=|>=|=|<|>|#")
private val SYMBOL_REGEX = Regex("\\(|\\)|:=|;|\\.|!|\\?|\\+|-|\\*|/")

class Pl0Scanner(input: String) : Scanner<TokenType>(
    input,
    TokenType.EOF,
    RegularExpressions.WHITESPACE to null,
    KEYWORD_REGEX to TokenType.KEYWORD,
    NUMBER_REGEX to TokenType.NUMBER,
    IDENT_REGEX to TokenType.IDENT,
    COMPARISON_REGEX to TokenType.COMPARISON,
    SYMBOL_REGEX to TokenType.SYMBOL,
)
