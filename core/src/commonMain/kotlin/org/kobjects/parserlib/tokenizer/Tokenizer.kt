package org.kobjects.parserlib.tokenizer

/**
 * A Tokenizer. Typically parameterized with an enum type denoting the type of token.
 * Regular expressions paired with a null token are not reported. This is useful for
 * skipping insignificant whitespace or comments.
 *
 * If used in conjunction with the expression parser, it probably makes sense to
 * create a subclass to get an unparameterized type.
 */
open class Tokenizer<T>(
    val input: String,
    val eofType: T,
    vararg val types: Pair<Regex, T?>,
    prepend: List<Token<T>> = listOf(),
    val normalization: (T, String) -> String = { _, s -> s},
)  {
    // A copy of the current token for error reporting (avoiding potential stack overflow when
    // lookahead(0) fails.
    val current: Token<T>
        get() = lookAhead(0)

    val eof: Boolean
        get() = current.type == eofType

    private var pos = 0
    private var col = 0
    private var line = 0
    // var skipped = false
    private var buffer = prepend.toMutableList()

    private val disabledTypes = mutableMapOf<T, Int>()
    private var currentMaterialized: Token<T> = current

    @OptIn(ExperimentalStdlibApi::class)
    private fun readToken(): Token<T> {
        while (pos < input.length) {
            val startPos = pos
            val startCol = col
            val startLine = line
            for (candidate in types) {
                val regex = candidate.first
                val match = regex.matchAt(input, pos)
                if (match != null) {
                    val startPos = pos
                    if (match.range.isEmpty()) {
                        throw IllegalArgumentException("Empty range for expression: $regex")
                    }
                    val text = match.value
                    var newlinePos = text.indexOf('\n')
                    if (newlinePos == -1) {
                        col += text.length
                    } else {
                        do {
                            line++
                            col = text.length - newlinePos
                            newlinePos = text.indexOf('\n', newlinePos + 1)
                        } while (newlinePos != -1)
                    }
                    pos += text.length

                    // Matches without type are not reported. Useful for whitespace and
                    // potentially comments.
                    val type = candidate.second ?: break
                    return Token(startPos, startLine, startCol, type, normalization(type, match.value))
                }
            }
            if (startPos == pos) {
                throw exception("No token matched '${input.substring(pos, pos + 10)}...'}");
            }
        }
        return Token(pos, line, col, eofType, "<EOF>")
    }

    /**
     * Consumes the current token: returns the text of the current token and advances to the
     * next token.
     */
    fun consume(): String {
        currentMaterialized = current
        while (buffer[0] !== currentMaterialized) {
            buffer.removeAt(0)
        }
        buffer.removeAt(0)
        return currentMaterialized.text
    }

    /**
     * Disable the given token type. Might be used to filter out line breaks inside parens.
     * Multiple calls are cumulative and a corresponding number of calls to enableToken are
     * needed to re-enable the token.
     */
    fun disable(type: T) {
        if (type == eofType)  {
            throw IllegalArgumentException("Can't filter out EOF.")
        }
        disabledTypes[type] = disabledTypes.getOrElse(type) { 0 } + 1
    }

    /** Re-enables a disabled token type. */
    fun enable(type: T) {
        val oldDepth = disabledTypes[type] ?: throw exception("Token type $type enabled already.")
        if (oldDepth == 1) {
            disabledTypes.remove(type)
        } else {
            disabledTypes[type] = oldDepth - 1
        }
    }

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

    /** Creates an illegal state exception with position context information. */
    fun exception(message: String) = ParsingException(currentMaterialized, message)

    fun ensureParsingException(e: Exception): ParsingException {
        if (e is ParsingException) {
            return e
        }
        return ParsingException(currentMaterialized, null, e)
    }

    fun lookAhead(index: Int): Token<T> {
        if (disabledTypes.isEmpty()) {
           return lookAheadUnfiltered(index)
        }
        var count = 0
        var pos = 0
        while(true) {
            val candidate = lookAheadUnfiltered(pos++)
            if (!disabledTypes.containsKey(candidate.type)) {
                if (count++ == index) {
                    return candidate
                }
            }
        }
    }

    fun lookAheadUnfiltered(index: Int): Token<T> {
        while (buffer.size <= index) {
            buffer.add(readToken())
        }
        return buffer[index]
    }
}