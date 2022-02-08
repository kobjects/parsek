package org.kobjects.parserlib.expressionparser

import org.kobjects.parserlib.tokenizer.Tokenizer
import java.lang.IllegalStateException


/**
 * A simple configurable expression parser.
 *
 * T is the token type of the underlying parser.
 *
 * R is the type of the parsed expression.
 */
abstract class ExpressionParser<T, R> (
    vararg configuration: Configuration<T, R>
) {
    private val prefix: Map<String, Symbol.Unary<T, R>>
    private val infixOrSuffix: Map<String, Symbol<T, R>>

    init {
        var prefixBuilder = mutableMapOf<String, Symbol.Unary<T, R>>()
        var infixOrSuffixBuilder = mutableMapOf<String, Symbol<T, R>>()

        for (config in configuration) {
            prefixBuilder.putAll(config.prefix)
            infixOrSuffixBuilder.putAll(config.infixOrSuffix)
        }

        prefix = prefixBuilder.toMap()
        infixOrSuffix = infixOrSuffixBuilder.toMap()
    }

    private fun parsePrefix(tokenizer: Tokenizer<T>): R {
        val token: String = tokenizer.current.value
        val prefixSymbol = prefix[token] ?: return parsePrimary(tokenizer)
        tokenizer.next()
        val operand = parse(tokenizer, prefixSymbol.precedence)
        return prefixSymbol.build(tokenizer, token, operand)
    }

    /**
     * Parser an expression from the given tokenizer. Leftover tokens will be ignored and
     * may be handled by the caller.
     */
    fun parse(tokenizer: Tokenizer<T>, precedence: Int = -1): R {
        var left = parsePrefix(tokenizer)
        while (true) {
            val token: String = tokenizer.current.value
            val symbol = infixOrSuffix[token] ?: break
            if (symbol.precedence < precedence) {
                break
            }
            tokenizer.next()
            left = if (symbol is Symbol.Unary<T, R>) {
                symbol.build(tokenizer, token, left)
            } else if (symbol is Symbol.Binary<T, R>) {
                if (symbol.rtl) {
                    val right = parse(tokenizer, symbol.precedence - 1)
                    symbol.build(tokenizer, token, left, right)
                } else {
                    val right = parse(tokenizer, symbol.precedence)
                    symbol.build(tokenizer, token, left, right)
                }
            } else {
                throw IllegalStateException("Unrecognized symbol type: $symbol")
            }
        }
        return left
    }

    abstract fun parsePrimary(tokenizer: Tokenizer<T>): R


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

    class Configuration<T, R>(
        val prefix: Map<String, Symbol.Unary<T, R>>,
        val infixOrSuffix: Map<String, Symbol<T, R>>
    )

    companion object  {
        fun <T, R> prefix(
            precedence: Int,
            vararg names: String,
            builder: (Tokenizer<T>, String, R) -> R
        ): Configuration<T, R> {
            var prefix = mutableMapOf<String, Symbol.Unary<T, R>>()
            for (name in names) {
                prefix[name] = Symbol.Unary(precedence, builder)
            }
            return Configuration(prefix, mapOf())
        }

        fun <T, R> infix(
            precedence: Int,
            vararg names: String,
            builder: (Tokenizer<T>, String, R, R) -> R
        ): Configuration<T, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<T, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Binary(precedence, rtl = false, builder)
            }
            return Configuration(mapOf(), infixOrSuffix)
        }

        fun <T, R> infixRtl(
            precedence: Int,
            vararg names: String,
            builder: (Tokenizer<T>, String, R, R) -> R
        ): Configuration<T, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<T, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Binary(precedence, rtl = true, builder)
            }
            return Configuration(mapOf(), infixOrSuffix)
        }

        fun <T, R> suffix(
            precedence: Int,
            vararg names: String,
            builder: (Tokenizer<T>, String, R) -> R
        ): Configuration<T, R> {
            val infixOrSuffix = mutableMapOf<String, Symbol<T, R>>()
            for (name in names) {
                infixOrSuffix[name] = Symbol.Unary(precedence, builder)
            }
            return Configuration(mapOf(), infixOrSuffix)
        }
    }
}