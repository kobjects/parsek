package org.kobjects.parsek.tokenizer

class ParsingException(
    val token: Token<*>,
    message: String?,
    cause: Throwable? = null) : Exception(message, cause) {

    override fun toString(): String {
        return (message ?: cause?.toString() ?: "Parsing Error") + "\n$token"
    }

}