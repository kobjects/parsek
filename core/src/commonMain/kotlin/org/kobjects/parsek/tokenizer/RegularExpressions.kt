package org.kobjects.parsek.tokenizer

/**
 * A set of regular expressions that might be useful for parsing.
 */
object RegularExpressions {
    /** At least one whitespace character */
    val WHITESPACE = Regex("\\s+")

    /** At least one letter or '_', followed by any number of the same or digits. */
    val IDENTIFIER = Regex("[\\p{Alpha}_$][\\p{Alpha}_$\\d]*")

    val JSON_STRING = Regex("\"(((?=\\\\)\\\\([\"\\\\\\/bfnrt]|u[0-9a-fA-F]{4}))|[^\"\\\\\\x00-\\x1F\\x7F]+)*\"")

    val JSON_NUMBER = Regex("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?")
    /**
     * Numbers excluding the sign prefix in order to avoid potential problems with
     * parsing expressions such as "3 - 4"
     */
    val NUMBER = Regex("(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?")

    /** Double-quoted strings.  */
    val DOUBLE_QUOTED_STRING = Regex("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"")

    /** Single-quoted strings. */
    val SINGLE_QUOTED_STRING = Regex("'([^'\\\\]*(\\\\.[^'\\\\]*)*)'")

    /** All kinds of symbols */
    // Escaping "}" seems to be required for Android.
    val SYMBOL = Regex("\\+|-|\\*|%|<=|>=|==|=|<>|<|>|\\^|!=|!|\\(|\\)|,|\\?|;|~|\\[|]|\\{|\\}|/")
}