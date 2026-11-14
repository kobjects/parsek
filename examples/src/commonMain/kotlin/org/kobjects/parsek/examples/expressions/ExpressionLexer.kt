package org.kobjects.parsek.examples.expressions

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions

/** We include newlines here to simplify parsing for the "Mython" example. */
class ExpressionLexer(input: String) : Lexer<TokenType>(
    input,
    RegularExpressions.HORIZONTAL_WHITESPACE to null,
    RegularExpressions.NEWLINE to TokenType.NEWLINE,
    RegularExpressions.UNSIGNED_NUMBER to TokenType.NUMBER,
    RegularExpressions.DOUBLE_QUOTED_STRING to TokenType.STRING,
    RegularExpressions.IDENTIFIER to TokenType.IDENTIFIER,
    RegularExpressions.SYMBOL to TokenType.SYMBOL,
)
