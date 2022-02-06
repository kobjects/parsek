package org.kobjects.parserlib.tokenizer

/**
 * A set of regular expressions that might be useful for parsing.
 */
object RegularExpressions {
    val WHITESPACE = Regex("\\s+")
    val IDENTIFIER = Regex("[\\p{Alpha}_$][\\p{Alpha}_$\\d]*")
    val NUMBER = Regex("(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?")
    val STRING = Regex("(\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)')")
    val SYMBOL = Regex("\\+|-|\\*|%|<=|>=|==|=|<|>|\\^|!")
}