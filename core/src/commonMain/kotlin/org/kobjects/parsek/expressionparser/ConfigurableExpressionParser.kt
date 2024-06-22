package org.kobjects.parsek.expressionparser

import org.kobjects.parsek.tokenizer.Scanner


/**
 * A simple configurable expression parser.
 *
 * S is the scanner type
 *
 * C is the parsing context, typically holding which variable definitions etc.
 *
 * R is the type of the parsed expression.
 */
open class ConfigurableExpressionParser<S : Scanner<*>, C, R> (
    val parsePrimary: (S, C) -> R,
    vararg val configuration: Configuration<S, C, R>,
) {
    private val prefix: Map<String, Symbol.Unary<S, C, R>>
    private val infixOrSuffix: Map<String, Symbol<S, C, R>>

    init {
        val prefixBuilder = mutableMapOf<String, Symbol.Unary<S, C, R>>()
        val infixOrSuffixBuilder = mutableMapOf<String, Symbol<S, C, R>>()

        for (config in configuration) {
            prefixBuilder.putAll(config.prefix)
            infixOrSuffixBuilder.putAll(config.infixOrSuffix)
        }

        prefix = prefixBuilder.toMap()
        infixOrSuffix = infixOrSuffixBuilder.toMap()
    }

    private fun parsePrefix(scanner: S, context: C): R {
        val token: String = scanner.current.text
        val prefixSymbol = prefix[token] ?: return parsePrimary(scanner, context)
        scanner.consume()
        val operand = parseExpression(scanner, context, prefixSymbol.precedence)
        return prefixSymbol.build(scanner, context, token, operand)
    }

    /**
     * Parser an expression from the given tokenizer. Leftover tokens will be ignored and
     * may be handled by the caller.
     */
    fun parseExpression(scanner: S, context: C, precedence: Int = -1): R {
        var left = parsePrefix(scanner, context)
        while (true) {
            val token: String = scanner.current.text
            val symbol = infixOrSuffix[token] ?: break
            if (symbol.precedence <= precedence) {
                break
            }
            scanner.consume()
            left = if (symbol is Symbol.Unary<S, C, R>) {
                symbol.build(scanner, context, token, left)
            } else if (symbol is Symbol.Binary<S, C, R>) {
                if (symbol.rtl) {
                    val right = parseExpression(scanner, context, symbol.precedence - 1)
                    symbol.build(scanner, context, token, left, right)
                } else {
                    val right = parseExpression(scanner, context, symbol.precedence)
                    symbol.build(scanner, context, token, left, right)
                }
            } else {
                throw IllegalStateException("Unrecognized symbol type: $symbol")
            }
        }
        return left
    }

    interface Symbol<S, C, R> {
        val precedence: Int

        class Unary<S, C, R>(
            override val precedence: Int,
            val build: (S, C, String, R) -> R
        ) : Symbol<S, C, R>

        class Binary<S, C, R>(
            override val precedence: Int,
            val rtl: Boolean,
            val build: (S, C, String, R, R) -> R
        ) : Symbol<S, C, R>
    }

    class Configuration<S, C, R>(
        val prefix: Map<String, Symbol.Unary<S, C, R>>,
        val infixOrSuffix: Map<String, Symbol<S, C, R>>
    )

    companion object  {
        fun <S, C, R> prefix(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R) -> R
        ): Configuration<S, C, R> {
            val prefix = mutableMapOf<String, Symbol.Unary<S, C, R>>()
            for (name in names) {
                prefix[name] = Symbol.Unary(precedence, builder)
            }
            return Configuration(prefix, emptyMap())
        }

        fun <S, C, R> infix(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R, R) -> R
        ): Configuration<S, C, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<S, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Binary(precedence, rtl = false, builder)
            }
            return Configuration(emptyMap(), infixOrSuffix)
        }

        fun <S, C, R> infixRtl(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R, R) -> R
        ): Configuration<S, C, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<S, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Binary(precedence, rtl = true, builder)
            }
            return Configuration(emptyMap(), infixOrSuffix)
        }

        fun <S, C, R> suffix(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R) -> R
        ): Configuration<S, C, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<S, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Unary(precedence, builder)
            }
            return Configuration(emptyMap(), infixOrSuffix)
        }
    }
}