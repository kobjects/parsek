package org.kobjects.parsek.tokenizer

/**
 * Tokens in parsek are represented by this class.
 *
 * [T] Typically is an enum type, denoting the token type, mapped from regular expressions in the Lexer.
 */
data class Token<T>(
    val localPos: Int,
    val pos: Int,
    val line: Int,
    val col: Int,
    val type: T,
    val text: String,
) {
    override fun toString() = "$type@$line:$col: «$text»"
}