package org.kobjects.parserlib.tokenizer

/**
 * Class that wraps a "plain" lexer (implemented as a token iterator) for more comfortable token
 * analysis in a parser.
 */
open class Scanner<T>(
    val input: Iterator<Token<T>>,
    val eofType: T,
    val eofText: String = ""
)  {
    private val buffer = mutableListOf<Token<T>>()

    // The last token encountered so far. Used to determine the position of the end of the file.
    private var lastToken = Token(0, 0, 0, 0, eofType, eofText)

    // Used for error reporting -- avoiding a potential stack overflow in error reporting when
    // input.next() throws.
    private var currentMaterialized = lastToken

    val current: Token<T>
        get() = lookAhead(0)

    val eof: Boolean
        get() = current.type == eofType && current.text == eofText

    /** Consumes the current token and returns its text content. */
    fun consume(): String {
        if (eof) {
            throw exception("Trying to read past EOF")
        }
        // Checking for eof ensures that there is at least one element in the buffer.
        return buffer.removeAt(0).text.apply { currentMaterialized = current }
    }

    /**
     * Consumes a token with the given type and returns its text contetn. If the current token
     * has a different type, an exception is thrown.
     */
    fun consume(type: T, errorMessage: String = "Token type $type expected."): String {
        if (current.type != type) {
            throw exception(errorMessage)
        }
        return consume()
    }

    /**
     * Consume and return a token with the given text value. If the current token type does not
     * match, an exception is thrown.
     */
    fun consume(text: String, errorMessage: String = "Token text '$text' expected.") {
        if (!tryConsume(text)) {
            throw exception(errorMessage)
        }
    }

    /**
     * If the current token text value matches the given string, it is consumed and true
     * is returned. Otherwise, false is returned.
     */
    fun tryConsume(value: String): Boolean {
        if (current.text == value) {
            consume()
            return true
        }
        return false
    }

    fun requireEof(message: () -> String = { "EOF expected." }) {
        if (!eof) throw exception(message())
    }

    /**
     * Wraps the given exception in a parsing exception if it's not a parsing exception already.
     * Parsing exceptions are returned unchanged. Useful to associate other exceptions encountered
     * during parsing with a token (including the corresponding input position).
     */
    fun ensureParsingException(e: Exception): ParsingException {
        if (e is ParsingException) {
            return e
        }
        return ParsingException(currentMaterialized, null, e)
    }

    /** Creates an illegal state exception with position context information. */
    fun exception(message: String) = ParsingException(currentMaterialized, message)


    fun lookAhead(index: Int): Token<T> {
        while (buffer.size <= index) {
            if (!input.hasNext()) {
                return Token(lastToken.localPos + lastToken.text.length, lastToken.pos + lastToken.text.length, lastToken.line, lastToken.col + lastToken.text.length, eofType, eofText)
            }
            lastToken = input.next()
            buffer.add(lastToken)
        }
        return buffer[index]
    }
}