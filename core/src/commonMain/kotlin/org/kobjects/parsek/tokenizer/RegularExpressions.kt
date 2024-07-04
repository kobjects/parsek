package org.kobjects.parsek.tokenizer

/**
 * A set of regular expressions that might be useful for parsing.
 */
object RegularExpressions {
    /** A newline including horizontal whitespace that follows, */
    val NEWLINE = Regex("""\n[ \t]*""")

    /** At least one whitespace character */
    val WHITESPACE = Regex("""\s+""")

    val HORIZONTAL_WHITESPACE = Regex("""[ \t]+""")

    /** At least one letter, '_' or '$', followed by any number of the same or digits. */
    val IDENTIFIER = Regex("""[\p{Alpha}_$][\p{Alpha}_$\d]*""")

    /**
     * Numbers excluding the sign prefix in order to avoid potential problems with
     * parsing expressions such as "3 - 4"
     */
    val NUMBER = Regex("""(\d+(\.\d*)?|\.\d+)([eE][+-]?\d+)?""")

    /**
     * Signed Numbers matching the JSON specification
     */
    val JSON_NUMBER = Regex("""-?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+-]?\d+)?""")

    /**
     * Regex for recognizing double-quoted strings, including quotes escaped using a backslash. Additional forms
     * of character escapes can be recognized (or rejected) in post-processing.
     */
    val DOUBLE_QUOTED_STRING = Regex(""""([^"\\]*(\\.[^"\\]*)*)"""")

    /**
     * Regex for recognizing single-quoted strings, including quotes escaped using a backslash. Additional forms
     * of character escapes can be recognized (or rejected) in post-processing.
     */
    val SINGLE_QUOTED_STRING = Regex("""'([^"\\]*(\\.[^'\\]*)*)'""")

    /**
     * Recognizes double-quoted strings using doubled double-quotes as escape for double-quotes, as used in the CSV
     * format and BASIC interpreters.
     */
    val CSV_STRING = Regex(""""([^"]*(""[^"]*)*)"""")

    /**
     * All kinds of symbols, including composites used in various programming languages. This might be a good
     * starting point, but should probably be replaced with a custom regex. */
    // Escaping "}" seems to be required for Android.
    val SYMBOL = Regex("""\+\+|\+|--|-|\*\*|\*|%|<=|>=|==|=|<>|<|>|&&|&|\|\||\||\^|!=|!|\(|\)|,|\?|;|:|~|\[|]|\{|\}|/""")
}