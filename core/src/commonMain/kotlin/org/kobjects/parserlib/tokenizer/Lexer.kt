package org.kobjects.parserlib.tokenizer

import kotlin.math.min

class Lexer<T>(
    private val input: String,
    private vararg val types: Pair<Regex, T?>,
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
                    val type = candidate.second ?: break
                    next = Token(startPos, startLine, startCol, type, normalization(type, match.value))
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
        return next!!
    }
}