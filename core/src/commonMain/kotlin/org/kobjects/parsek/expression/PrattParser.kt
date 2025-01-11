package org.kobjects.parsek.expression

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
open class PrattParser<S : Scanner<*>, C, R> (
    val parsePrimary: (S, C) -> R,
    val buildUnary: (S, C, String, R) -> R,
    val buildBinary: (S, C, String, R, R) -> R,
    vararg operators: Operator,
) {
    private val prefix: Map<String, Operator>
    private val infixOrSuffix: Map<String, Operator>

    init {
        val prefixBuilder = mutableMapOf<String, Operator>()
        val infixOrSuffixBuilder = mutableMapOf<String, Operator>()

        for (op in operators) {
            val target = (if (op is Operator.Prefix) prefixBuilder else infixOrSuffixBuilder)
            for (name in op.names) {
                target.put(name, op)
            }
        }

        prefix = prefixBuilder.toMap()
        infixOrSuffix = infixOrSuffixBuilder.toMap()
    }

    private fun parsePrefix(scanner: S, context: C): R {
        val token: String = scanner.current.text
        val prefixSymbol = prefix[token] ?: return parsePrimary(scanner, context)
        scanner.consume()
        val operand = parseExpression(scanner, context, prefixSymbol.precedence)
        return buildUnary(scanner, context, token, operand)
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
            left = when (symbol) {
                is Operator.Suffix -> buildUnary(scanner, context, token, left)
                is Operator.InfixRtl -> {
                    val right = parseExpression(scanner, context, symbol.precedence - 1)
                    buildBinary(scanner, context, token, left, right)
                }
                is Operator.Infix -> {
                    val right = parseExpression(scanner, context, symbol.precedence)
                    buildBinary(scanner, context, token, left, right)

                }
                else -> throw IllegalStateException("Unrecognized symbol type: $symbol")
            }
        }
        return left
    }
}