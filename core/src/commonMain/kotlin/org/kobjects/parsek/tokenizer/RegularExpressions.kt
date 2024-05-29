package org.kobjects.parsek.tokenizer

/**
 * A set of regular expressions that might be useful for parsing.
 */
object RegularExpressions {
    /** At least one whitespace character */
    val WHITESPACE = Regex("\\s+")

    /** At least one letter, '_' or $, followed by any number of the same or digits. */
    val IDENTIFIER = Regex("[\\p{Alpha}_$][\\p{Alpha}_$\\d]*")

    /**
     * Note that the sign prefix is not included in order to avoid potential problems with
     * parsing expressions such as "3 - 4"
     */
    val NUMBER = Regex("(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?")
    val DOUBLE_QUOTED_STRING = Regex("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"")
    val SINGLE_QUOTED_STRING = Regex("'([^'\\\\]*(\\\\.[^'\\\\]*)*)'")
    // Escaping "}" seems to be required for Android.
    val SYMBOL = Regex("\\+|-|\\*|%|<=|>=|==|=|<>|<|>|\\^|!=|!|\\(|\\)|,|\\?|;|~|\\[|]|\\{|\\}|/")
}