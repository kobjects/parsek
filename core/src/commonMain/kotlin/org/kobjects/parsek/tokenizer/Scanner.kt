package org.kobjects.parsek.tokenizer

/**
 * Wraps a token [Iterator] (or tokenizes an input `String` directly via regular expression rules)
 * with current-token inspection, unlimited lookahead, and consumption helpers for recursive-descent
 * and Pratt parsers.
 *
 * Subclassing `Scanner` (for example `class MyScanner(input: String) : Scanner<TokenType>(...)`)
 * is recommended for non-trivial parsers so parser functions can accept `MyScanner` instead of
 * repeating `Scanner<TokenType>`, and custom helper methods can be added as needed. Direct
 * instantiation (`Scanner(input, ...)`) is also supported for simple cases.
 *
 * Pass a dedicated enum value (e.g. `TokenType.EOF`) as [eofType] to keep `Token.type`
 * non-nullable; pass `null` with a nullable type parameter (`Scanner<TokenType?>(input, null, ...)`)
 * to avoid adding an `EOF` enum entry; or use `Scanner<Unit?>(input, null, ...)` (mapping emitted
 * tokens to `Unit`) when token types aren't needed at all.
 */
open class Scanner<T>(
    val input: Iterator<Token<T>>,
    val eofType: T,
) {
    private var eofText = "<EOF>"

    private val buffer = mutableListOf<Token<T>>()

    // The last real token encountered so far. Used to determine the position of the end of the file.
    private var lastToken = Token(0, 0, 0, 0, eofType, "")

    // Used for error reporting -- avoiding a potential stack overflow in error reporting when
    // input.next() throws.
    private var currentMaterialized = Token(0, 0, 0, 0, eofType, eofText)

    @Deprecated(
        "EOF text is now fixed to \"<EOF>\". Use Scanner(input, eofType) instead.",
        ReplaceWith("Scanner(input, eofType)"),
    )
    constructor(
        input: Iterator<Token<T>>,
        eofType: T,
        eofText: String,
    ) : this(input, eofType) {
        this.eofText = eofText
        this.currentMaterialized = Token(0, 0, 0, 0, eofType, eofText)
    }

    /**
     * Convenience constructor that tokenizes [input] directly using [Lexer] with the given [types]
     * rules (`Regex to tokenType`, or `Regex to null` for skipped tokens such as whitespace).
     *
     * Store [Regex] instances in constants (such as [RegularExpressions] or top-level `private val`s)
     * rather than constructing them on each parse call.
     */
    constructor(input: String, eofType: T, vararg types: Pair<Regex, T?>) : this(
        Lexer(input, *types),
        eofType
    )

    val current: Token<T>
        get() = lookAhead(0)

    val eof: Boolean
        get() = current.type == eofType && current.text == eofText

    /** Consumes and returns the current token. */
    fun consume(): Token<T> {
        if (eof) {
            throw exception("Trying to read past EOF")
        }
        // Checking for eof ensures that there is at least one element in the buffer.
        return buffer.removeAt(0).apply { currentMaterialized = current }
    }

    /**
     * Consumes a token with the given type and returns it. If the current token
     * has a different type, an exception is thrown.
     */
    fun consume(type: T, errorMessage: (Token<T>) -> String = { "Token type $type expected." }): Token<T> {
        if (current.type != type) {
            throw exception(errorMessage(current))
        }
        return consume()
    }

    /**
     * Consumes and returns a token with the given text value. If the current token text does not
     * match, an exception is thrown.
     */
    fun consume(
        text: String,
        errorMessage: (Token<T>) -> String = { "Expected: '$text'" }
    ): Token<T> {
        val result = current
        if (!tryConsume(text)) {
            throw exception(errorMessage(current))
        }
        return result
    }

    /**
     * Consumes and returns a token matching [text] case-insensitively. If the current token text
     * does not match, an exception is thrown.
     */
    fun consumeIgnoreCase(
        text: String,
        errorMessage: (Token<T>) -> String = { "Expected: '$text'" }
    ): Token<T> {
        val result = current
        if (!tryConsumeIgnoreCase(text)) {
            throw exception(errorMessage(current))
        }
        return result
    }

    @Deprecated("Use consume(text, errorMessage) or consumeIgnoreCase(text, errorMessage) instead.")
    fun consume(
        text: String,
        ignoreCase: Boolean,
        errorMessage: (Token<T>) -> String = { "Expected: '$text'" }
    ): Token<T> = if (ignoreCase) consumeIgnoreCase(text, errorMessage) else consume(text, errorMessage)

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

    /**
     * If the current token text value matches the given string case-insensitively, it is consumed
     * and true is returned. Otherwise, false is returned.
     */
    fun tryConsumeIgnoreCase(value: String): Boolean {
        if (current.text.equals(value, ignoreCase = true)) {
            consume()
            return true
        }
        return false
    }

    @Deprecated("Use tryConsume(value) or tryConsumeIgnoreCase(value) instead.")
    fun tryConsume(value: String, ignoreCase: Boolean): Boolean =
        if (ignoreCase) tryConsumeIgnoreCase(value) else tryConsume(value)

    fun requireEof(message: () -> String = { "EOF expected." }) {
        require(eof, message)
    }

    fun require(condition: Boolean, message: () -> String) {
        if (!condition) throw exception(message())
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

    /** Creates a parsing exception with position context information. */
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

    override fun toString() = currentMaterialized.toString()
}
