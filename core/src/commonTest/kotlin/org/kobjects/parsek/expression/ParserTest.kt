package org.kobjects.parsek.expression

import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    @Test
    fun testParser() {
        assertEquals(-4.0, Calculator.calculate("4 - 4 - 4"))
        assertEquals(5.0, Calculator.calculate("3 + 2"))
        assertEquals(11.0, Calculator.calculate("3 + 2 * 4"))
        assertEquals(2.0, Calculator.calculate("1 ? 2 : 3"))
        assertEquals(2.0, Calculator.calculate("0 ? 1 : 2"))
    }

    enum class TokenType {
        NUMBER, IDENTIFIER, SYMBOL, EOF
    }

    class CalculatorScanner(input: String) : Scanner<TokenType>(
        input,
        TokenType.EOF,
        RegularExpressions.WHITESPACE to null,
        RegularExpressions.SYMBOL to TokenType.SYMBOL,
        RegularExpressions.IDENTIFIER to TokenType.IDENTIFIER,
        RegularExpressions.UNSIGNED_NUMBER to TokenType.NUMBER,
    )

    object Calculator : PrattParser<CalculatorScanner, Unit, Double>(
        { scanner, _ -> scanner.consume(TokenType.NUMBER).text.toDouble() },
        { scanner, _, name, operand ->
            if (name == "?") Calculator.parseTernary(scanner, operand)
            else Calculator.evalUnary(name, operand)
        },
        { _, _, name, left, right -> Calculator.evalBinary(name, left, right) },
        Operator.Infix(4, "^"),
        Operator.Prefix(3, "+", "-"),
        Operator.Infix(2, "*", "/"),
        Operator.Infix(1, "+", "-"),
        Operator.Suffix(0, "?")
    ) {
        fun parseTernary(scanner: CalculatorScanner, condition: Double): Double {
            val trueValue = parseExpression(scanner, Unit, 0)
            scanner.consume(":")
            val falseValue = parseExpression(scanner, Unit, 0)

            return if (condition != 0.0) trueValue else falseValue
        }

        fun evalUnary(name: String, operand: Double) = when (name) {
            "-" -> -operand
            "+" -> operand
            else -> throw IllegalArgumentException("Unrecognized unary operator: $name")
        }

        fun evalBinary(name: String, left: Double, right: Double) = when (name) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            "^" -> left.pow(right)
            else -> throw IllegalArgumentException("Unrecognized binary operator: $name")
        }

        fun calculate(expression: String): Double {
            val scanner = CalculatorScanner(expression)
            val result = Calculator.parseExpression(scanner)
            scanner.requireEof()
            return result
        }
    }
}
