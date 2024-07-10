package org.kobjects.parsek.tokenizer

/**
 * Exception type for parsing errors containing a reference to the current token.
 */
class ParsingException(
    val token: Token<*>,
    message: String?,
    cause: Throwable? = null) : Exception(message, cause) {

    override fun toString(): String {
        return (message ?: cause?.toString() ?: "Parsing Error") + "\n$token"
    }

}