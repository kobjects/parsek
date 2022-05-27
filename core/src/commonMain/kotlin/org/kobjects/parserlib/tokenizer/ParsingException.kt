package org.kobjects.parserlib.tokenizer

class ParsingException(
    val token: Token<*>,
    msg: String,
    chained: Exception? = null) : Exception(msg, chained) {
}