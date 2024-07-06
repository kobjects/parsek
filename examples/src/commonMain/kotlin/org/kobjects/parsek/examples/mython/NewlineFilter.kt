package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.ExpressionLexer
import org.kobjects.parsek.examples.expressions.TokenType
import org.kobjects.parsek.tokenizer.Filter
import org.kobjects.parsek.tokenizer.Token
import kotlin.math.max

class NewlineFilter(lexer: ExpressionLexer) : Filter<TokenType>(lexer) {
    var bracketDepth = 0

    override fun accept(token: Token<TokenType>): Boolean {
        when (token.text) {
            "{",
            "[",
            "(" -> bracketDepth++
            "}",
            "]",
            ")" -> bracketDepth = max(0, bracketDepth - 1)
        }
        return bracketDepth == 0 || token.type != TokenType.NEWLINE
    }
}