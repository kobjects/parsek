package org.kobjects.parsek.examples.expressions

import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.parsek.tokenizer.Token

class ExpressionScanner(
    tokens: Iterator<Token<TokenType>>,
) : Scanner<TokenType>(tokens, TokenType.EOF) {
    constructor(input: String) : this(ExpressionLexer(input))
}
