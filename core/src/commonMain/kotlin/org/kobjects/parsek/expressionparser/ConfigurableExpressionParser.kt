package org.kobjects.parsek.expressionparser

import org.kobjects.parsek.tokenizer.Scanner

/**
 * A simple configurable expression parser.
 *
 * - [S] is the scanner type
 * - [C] is the type of the parsing context, typically holding variable definitions etc.
 * - [R] is the type of the parsed expression.
 *
 * The parser is primarily configured by defining operators using the [prefix], [infix] and [suffix] companion functions,
 * defining operator names, types and precedences.
 */
open class ConfigurableExpressionParser<S : Scanner<*>, C, R> (
    val parsePrimary: (S, C) -> R,
    vararg operators: Operators<S, C, R>,
) {
    private val prefix: Map<String, Operator.Unary<S, C, R>>
    private val infixOrSuffix: Map<String, Operator<S, C, R>>

    init {
        val prefixBuilder = mutableMapOf<String, Operator.Unary<S, C, R>>()
        val infixOrSuffixBuilder = mutableMapOf<String, Operator<S, C, R>>()

        for (config in operators) {
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
            left = if (symbol is Operator.Unary<S, C, R>) {
                symbol.build(scanner, context, token, left)
            } else if (symbol is Operator.Binary<S, C, R>) {
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

    /**
     * Internal operator representation, consisting of the precedence and a build function.
     */
    interface Operator<S, C, R> {
        val precedence: Int

        /** The precedence and build function for a unary operator. */
        class Unary<S, C, R>(
            override val precedence: Int,
            val build: (S, C, String, R) -> R
        ) : Operator<S, C, R>

        /** The precedence, binding direction and build function for a binary operator. */
        class Binary<S, C, R>(
            override val precedence: Int,
            val rtl: Boolean,
            val build: (S, C, String, R, R) -> R
        ) : Operator<S, C, R>
    }

    /**
     * Maps operator names to their arity, precedence and build function.
     *
     * Use the companion [infix], [prefix] and [suffix] methods to configure the parser via the operators
     * vararg constructor parameter.
     */
    class Operators<S, C, R>(
        val prefix: Map<String, Operator.Unary<S, C, R>>,
        val infixOrSuffix: Map<String, Operator<S, C, R>>
    )

    companion object  {
        /** Defines a prefix operator */
        fun <S, C, R> prefix(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R) -> R
        ): Operators<S, C, R> {
            val prefix = mutableMapOf<String, Operator.Unary<S, C, R>>()
            for (name in names) {
                prefix[name] = Operator.Unary(precedence, builder)
            }
            return Operators(prefix, emptyMap())
        }

        /** Defines a "regular" left binding infix operator */
        fun <S, C, R> infix(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R, R) -> R
        ): Operators<S, C, R> {
            val infixOrSuffix = mutableMapOf<String, Operator<S, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Operator.Binary(precedence, rtl = false, builder)
            }
            return Operators(emptyMap(), infixOrSuffix)
        }

        /** Defines a RTL infix operator. */
        fun <S, C, R> infixRtl(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R, R) -> R
        ): Operators<S, C, R> {
            val infixOrSuffix = mutableMapOf<String, Operator<S, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Operator.Binary(precedence, rtl = true, builder)
            }
            return Operators(emptyMap(), infixOrSuffix)
        }

        /** Defines a suffix operator. */
        fun <S, C, R> suffix(
            precedence: Int,
            vararg names: String,
            builder: (S, C, String, R) -> R
        ): Operators<S, C, R> {
            val infixOrSuffix = mutableMapOf<String, Operator<S, C, R>>()
            for (name in names) {
                infixOrSuffix[name] = Operator.Unary(precedence, builder)
            }
            return Operators(emptyMap(), infixOrSuffix)
        }
    }
}