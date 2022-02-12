package org.kobjects.parserlib.tokenizer

/**
 * Tokenizer. Typically parameterized with an enum type denoting the type of token.
 * Regular expressions paired with a null token are not reported. This is useful for
 * skipping insignificant whitespace or comments.
 */
open class Tokenizer<T>(
    val bofType: T,
    val types: List<Pair<Regex, T?>>,
    val eofType: T,
    val input: String
) {
    var pos = 0
    var col = 0
    var line = 0
    var current: Token<T> = Token(0, 0, 0, bofType, "<BOF>")
    var skipped = false
    var eof = false

    @OptIn(ExperimentalStdlibApi::class)
    fun next(): Token<T> {
        skipped = false
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
                    val type = candidate.second
                    if (type == null) {
                        skipped = true
                        break
                    }
                    current = Token<T>(startPos, startLine, startCol, type, match.value)
                    return current
                }
            }
            if (startPos == pos) {
                throw error("No token matched '${input.substring(pos, pos + 10)}...'}");
            }
        }
        current = Token(pos, 0, 0, eofType, "<EOF>")
        eof = true
        return current
    }


    fun consume(type: T, errorMessage: String = "Token type $type expected."): String {
        if (current.type != type) {
            throw error(errorMessage)
        }
        val result = current.value;
        next()
        return result
    }

    /**
     * Consume and return a token with the given text value. If the current token type does not
     * match, an exception is thrown.
     */
    fun consume(value: String, errorMessage: String = "Token value '$value' expected.") {
        if (!tryConsume(value)) {
            throw error(errorMessage)
        }
    }

    /**
     * If the current token text value matches the given string, it is consumed and true
     * is returned. Otherwise, false is returned.
     */
    fun tryConsume(value: String): Boolean {
        if (current.value == value) {
            next()
            return true
        }
        return false
    }


    fun error(message: String): IllegalStateException {
        return IllegalStateException("$message\nCurrent token: $current\nInput: '$input'\nPosition: $pos")
    }

}