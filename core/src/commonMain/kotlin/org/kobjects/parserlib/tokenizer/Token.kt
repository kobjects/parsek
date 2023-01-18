package org.kobjects.parserlib.tokenizer

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