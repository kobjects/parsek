package org.kobjects.parserlib.tokenizer

/**
 * Tokenizer. Typically parameterized with an enum type denoting the type of token.
 */
class Tokenizer<T>(
    val types: List<Pair<Regex, T>>,
    val bofType: T,
    val eofType: T,
    val input: String
) {
    var pos = 0
    var current: Token<T> = Token(0, 0, 0, bofType, "<BOF>")

    fun next(): Token<T> {
        while (pos < input.length) {
            val oldPos = pos
            for (candidate in types) {
                val regex = candidate.first
                val match = regex.find(input.subSequence(pos, input.length))
                if (match != null) {
                    if (match.range.start > 0) {
                        throw IllegalArgumentException("Token expression does not start with \\A: $regex")
                    }
                    if (match.range.isEmpty()) {
                        throw IllegalArgumentException("Empty range for expression: $regex")
                    }
                    pos += match.value.length
                    val type = candidate.second ?: break
                    current = Token<T>(pos, 0, pos, type, match.value)
                    return current
                }
            }
            if (oldPos == pos) {
                throw error("No token matched");
            }
        }
        current = Token(pos, 0, 0, eofType, "<EOF>")
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
        val token = current
        if (token != null && token.value == value) {
            next()
            return true
        }
        return false
    }


    fun error(message: String): IllegalStateException {
        return IllegalStateException("$message\nCurrent token: $current\nInput: '$input'\nPosition: $pos")
    }

}