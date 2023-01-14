package org.kobjects.parserlib.tokenizer

import kotlin.math.min

/**
 * Splits the input string into tokens, exposed as token iterator. The type parameter indicates
 * the token type; typically an enum consisting of values such as IDENTIFIER, NUMBER etc.
 */
class Lexer<T>(
    /** The input. */
    private val input: String,
    /**
     * Pairs of regular expressions and the corresponding token types. Multiple regular
     * expressions may map to the same token type. Regular expressions mapping to null will be
     * consumed without creating a token. The matches will be tried in the given order.
     */
    private vararg val types: Pair<Regex, (String) -> T?>,
    /**
     * An optional normalization function for the token text.
     */
    private val normalization: (T, String) -> String = { _, s -> s }
): Iterator<Token<T>> {
    private var pos = 0
    private var col = 0
    private var line = 0
    private var next: Token<T>? = null

    @OptIn(ExperimentalStdlibApi::class)
    override fun hasNext(): Boolean {
        if (next != null) {
            return true
        }
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
                    val type = candidate.second(text) ?: break
                    next = Token(startPos, startLine, startCol, type, normalization(type, text))
                    return true
                }
            }
            if (startPos == pos) {
                val quote = if (pos < input.length + 40) input else (input.substring(pos, pos + 30) + "…")
                throw IllegalStateException(
                    "No token matched the following text at line $line: «$quote»}");
            }
        }
        return false
    }

    override fun next(): Token<T> {
        if (!hasNext()) {
            throw IllegalStateException("Trying to read beyond end of input.")
        }
        return next!!.apply { next = null }
    }
}