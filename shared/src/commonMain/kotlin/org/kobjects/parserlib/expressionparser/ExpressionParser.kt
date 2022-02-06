package org.kobjects.expressionparser

import org.kobjects.parserlib.tokenizer.Tokenizer
import java.lang.IllegalStateException

/**
 * A simple configurable expression parser.
 */
class ExpressionParser<T, R>(
    var primary: (ExpressionParser<T,R>, Tokenizer<T>) -> R
) {
    private val prefix = mutableMapOf<String, Symbol.Unary<T, R>>()
    private val infixOrSuffix = mutableMapOf<String, Symbol<T, R>>()

    fun addPrefix(precedence: Int, vararg names: String, builder: (Tokenizer<T>, String, R) -> R) {
        for (name in names) {
            prefix[name] = Symbol.Unary(precedence, builder)
        }
    }

    fun addSuffix(precedence: Int, vararg names: String, builder: (Tokenizer<T>, String, R) -> R) {
        for (name in names) {
            infixOrSuffix[name] = Symbol.Unary(precedence, builder)
        }
    }

    fun addInfix(precedence: Int, vararg names: String, builder: (Tokenizer<T>, String, R, R) -> R) {
        for (name in names) {
            infixOrSuffix[name] = Symbol.Binary(precedence, rtl = false, builder)
        }
    }

    fun addInfixRtl(precedence: Int, vararg names: String, builder: (Tokenizer<T>, String, R, R) -> R) {
        for (name in names) {
            infixOrSuffix[name] = Symbol.Binary(precedence, rtl = true, builder)
        }
    }


    /**
     * Parser an expression from the given tokenizer. Leftover tokens will be ignored and
     * may be handled by the caller.
     */
    fun parse(tokenizer: Tokenizer<T>): R {
        return parseOperator(tokenizer, -1)
    }

    private fun parsePrefix(tokenizer: Tokenizer<T>): R {
        val token: String = tokenizer.current.value
        val prefixSymbol = prefix[token] ?: return primary(this, tokenizer)
        tokenizer.next()
        val operand = parseOperator(tokenizer, prefixSymbol.precedence)
        return prefixSymbol.build(tokenizer, token, operand)
    }

    private fun parseOperator(tokenizer: Tokenizer<T>, precedence: Int): R {
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
                    val right = parseOperator(tokenizer, symbol.precedence - 1)
                    symbol.build(tokenizer, token, left, right)
                } else {
                    val right = parseOperator(tokenizer, symbol.precedence)
                    symbol.build(tokenizer, token, left, right)
                }
            } else {
                throw IllegalStateException("Unrecognized symbol type: $symbol")
            }
        }
        return left
    }


}