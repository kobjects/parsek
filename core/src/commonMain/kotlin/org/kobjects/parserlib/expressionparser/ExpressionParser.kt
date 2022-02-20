package org.kobjects.parserlib.expressionparser

import org.kobjects.parserlib.tokenizer.Tokenizer


/**
 * A simple configurable expression parser.
 *
 * T is the tokenenizer type
 *
 * C is the parsing context, typically holding which variable definitions etc.
 *
 * R is the type of the parsed expression.
 */
open class ExpressionParser<T : Tokenizer<*>, C, R> (
    vararg configuration: Configuration<T, C, R>,
    val parsePrimary: (T, C) -> R
) {
    private val prefix: Map<String, Symbol.Unary<T, C, R>>
    private val infixOrSuffix: Map<String, Symbol<T, C, R>>

    init {
        var prefixBuilder = mutableMapOf<String, Symbol.Unary<T, C, R>>()
        var infixOrSuffixBuilder = mutableMapOf<String, Symbol<T, C, R>>()

        for (config in configuration) {
            prefixBuilder.putAll(config.prefix)
            infixOrSuffixBuilder.putAll(config.infixOrSuffix)
        }

        prefix = prefixBuilder.toMap()
        infixOrSuffix = infixOrSuffixBuilder.toMap()
    }

    private fun parsePrefix(tokenizer: T, context: C): R {
        val token: String = tokenizer.current.text
        val prefixSymbol = prefix[token] ?: return parsePrimary(tokenizer, context)
        tokenizer.next()
        val operand = parse(tokenizer, context, prefixSymbol.precedence)
        return prefixSymbol.build(tokenizer, context, token, operand)
    }

    /**
     * Parser an expression from the given tokenizer. Leftover tokens will be ignored and
     * may be handled by the caller.
     */
    fun parse(tokenizer: T, context: C, precedence: Int = -1): R {
        var left = parsePrefix(tokenizer, context)
        while (true) {
            val token: String = tokenizer.current.text
            val symbol = infixOrSuffix[token] ?: break
            if (symbol.precedence <= precedence) {
                break
            }
            tokenizer.next()
            left = if (symbol is Symbol.Unary<T, C, R>) {
                symbol.build(tokenizer, context, token, left)
            } else if (symbol is Symbol.Binary<T, C, R>) {
                if (symbol.rtl) {
                    val right = parse(tokenizer, context, symbol.precedence - 1)
                    symbol.build(tokenizer, context, token, left, right)
                } else {
                    val right = parse(tokenizer, context, symbol.precedence)
                    symbol.build(tokenizer, context, token, left, right)
                }
            } else {
                throw IllegalStateException("Unrecognized symbol type: $symbol")
            }
        }
        return left
    }

    interface Symbol<T, C, R> {
        val precedence: Int

        class Unary<T, C, R>(
            override val precedence: Int,
            val build: (T, C, String, R) -> R
        ) : Symbol<T, C, R>

        class Binary<T, C, R>(
            override val precedence: Int,
            val rtl: Boolean,
            val build: (T, C, String, R, R) -> R
        ) : Symbol<T, C, R>
    }

    class Configuration<T, C, R>(
        val prefix: Map<String, Symbol.Unary<T, C, R>>,
        val infixOrSuffix: Map<String, Symbol<T, C, R>>
    )

    companion object  {
        fun <T, C, R> prefix(
            precedence: Int,
            vararg names: String,
            builder: (T, C, String, R) -> R
        ): Configuration<T, C, R> {
            var prefix = mutableMapOf<String, Symbol.Unary<T, C, R>>()
            for (name in names) {
                prefix[name] = Symbol.Unary(precedence, builder)
            }
            return Configuration(prefix, mapOf())
        }

        fun <T, C, R> infix(
            precedence: Int,
            vararg names: String,
            builder: (T, C, String, R, R) -> R
        ): Configuration<T, C, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<T, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Binary(precedence, rtl = false, builder)
            }
            return Configuration(mapOf(), infixOrSuffix)
        }

        fun <T, C, R> infixRtl(
            precedence: Int,
            vararg names: String,
            builder: (T, C, String, R, R) -> R
        ): Configuration<T, C, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<T, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Binary(precedence, rtl = true, builder)
            }
            return Configuration(mapOf(), infixOrSuffix)
        }

        fun <T, C, R> suffix(
            precedence: Int,
            vararg names: String,
            builder: (T, C, String, R) -> R
        ): Configuration<T, C, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<T, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Unary(precedence, builder)
            }
            return Configuration(mapOf(), infixOrSuffix)
        }
    }
}