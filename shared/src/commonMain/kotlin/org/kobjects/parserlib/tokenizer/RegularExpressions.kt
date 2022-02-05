package org.kobjects.parserlib.tokenizer

/**
 * A set of regular expressions that might be useful for parsers.
 */
object RegularExpressions {
    val WHITESPACE = Regex("\\A\\s+")
    val IDENTIFIER = Regex("\\A\\s*[\\p{Alpha}_$][\\p{Alpha}_$\\d]*")
    val NUMBER = Regex("\\A\\s*(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?")
    val STRING = Regex("\\A\\s*(\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)')")
}