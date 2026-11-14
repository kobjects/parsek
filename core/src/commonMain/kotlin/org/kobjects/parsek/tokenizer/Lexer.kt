package org.kobjects.parsek.tokenizer

/**
 * Splits [input] into tokens, exposed as a lazy [Iterator].
 *
 * Most parsers can pass regular expression rules directly to [Scanner] without instantiating
 * [Lexer] explicitly; use [Lexer] directly when you need to inspect or filter the raw token
 * stream before passing it to a [Scanner] (for example via `lexer.asSequence().filter { ... }.iterator()`).
 *
 * The type parameter [T] indicates the token type (typically an enum such as `IDENTIFIER`,
 * `NUMBER`, etc., or `Unit` when token types aren't needed). Custom [Regex] instances should be
 * stored in constants (such as [RegularExpressions] or top-level `private val`s) rather than
 * constructed on each parse call.
 */
open class Lexer<T>(
    /** The input string to tokenize. */
    private val input: String,
    /**
     * Pairs of regular expressions and the corresponding token types. Multiple regular
     * expressions may map to the same token type. Regular expressions mapped to `null` will be
     * consumed without creating a token. The matches will be tried in the given order.
     */
    private vararg val types: Pair<Regex, T?>,
    /** An optional start offset for token positions. */
    private val offset: Token<T>? = null,
) : Iterator<Token<T>> {
    private var pos = 0
    private var col = offset?.col ?: 0
    private var line = offset?.line ?: 0
    private var next: Token<T>? = null

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
                    next = Token(
                        startPos,
                        startPos + (offset?.pos ?: 0),
                        startLine,
                        startCol,
                        type,
                        text
                    )
                    return true
                }
            }
            if (startPos == pos) {
                val quote =
                    if (pos + 40 >= input.length) input.substring(pos) else (input.substring(pos, pos + 30) + "…")
                throw IllegalStateException(
                    "No token matched the following text at line $line: «$quote»"
                )
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
