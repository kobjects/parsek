package org.kobjects.expressionparser

import org.kobjects.parserlib.tokenizer.Tokenizer

interface Symbol<T, R> {
    val precedence: Int

    class Unary<T, R>(
        override val precedence: Int,
        val build: (Tokenizer<T>, String, R) -> R
    ) : Symbol<T, R>

    class Binary<T, R>(
        override val precedence: Int,
        val rtl: Boolean,
        val build: (Tokenizer<T>, String, R, R) -> R
    ) : Symbol<T, R>
}