package org.kobjects.parserlib.tokenizer

/**
 * Tokenizer. Typically parameterized with an enum type denoting the type of token.
 * Regular expressions paired with a null token are not reported. This is useful for
 * skipping insignificant whitespace or comments.
 *
 * If used in conjunction with the expression parser, it probably makes sense to
 * create a subclass to create an unparameterized type.
 */
open class Tokenizer<T>(
    val input: String,
    val bofType: T,
    val eofType: T,
    vararg val types: Pair<Regex, T?>,
    prepend: List<Token<T>> = listOf(),
) : Iterator<Token<T>> {
    val current: Token<T>
        get() = lookAhead(0)

    val bof: Boolean
        get() = buffer[0].type == bofType
    val eof: Boolean
        get() = current.type == eofType

    private var pos = 0
    private var col = 0
    private var line = 0
    // var skipped = false
    private var buffer = MutableList(prepend.size + 1) {
        if (it == 0) Token(0, 0, 0, bofType, "<BOF>") else prepend[it - 1]
    }
    private val disabledTypes = mutableMapOf<T, Int>()

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
                    return Token(startPos, startLine, startCol, type, match.value)
                }
            }
            if (startPos == pos) {
                throw exception("No token matched '${input.substring(pos, pos + 10)}...'}");
            }
        }
        return Token(pos, line, col, eofType, "<EOF>")
    }

    /** Consumes the current token: returns the current token and advances to the next token. */
    override fun next(): Token<T> {
        val result = current
        while (buffer[0] !== result) {
            buffer.removeAt(0)
        }
        buffer.removeAt(0)
        return result
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
        return next().text
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
            next()
            return true
        }
        return false
    }

    /** Creates an illegal state exception with position context information. */
    fun exception(message: String) = ParsingException(current, "$message\nToken: $current")

    override fun hasNext(): Boolean {
        return !eof
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