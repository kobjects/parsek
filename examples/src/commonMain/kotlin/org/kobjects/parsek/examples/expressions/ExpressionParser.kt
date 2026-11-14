package org.kobjects.parsek.examples.expressions


import org.kobjects.parsek.expression.Operator
import org.kobjects.parsek.expression.PrattParser
import org.kobjects.parsek.tokenizer.Scanner


object ExpressionParser : PrattParser<Scanner<TokenType>, Unit, Evaluable>(
    { scanner, _ -> ExpressionParser.parsePrimary(scanner) },
    { _, _, name, operand -> Symbol(name, ExpressionParser.unaryPrecedence(name), operand) },
    { _, _, name, left, right -> Symbol(name, ExpressionParser.binaryPrecedence(name), left, right) },
    Operator.Prefix(9, "+", "-"),
    Operator.Infix(8, "**"),
    Operator.Infix(7, "*", "/", "%", "//"),
    Operator.Infix(6, "+", "-"),
    Operator.Infix(5, "<", "<=", ">", ">="),
    Operator.Infix(4, "==", "!="),
    Operator.Infix(3, "and"),
    Operator.Infix(2, "or"),
    Operator.Prefix(1, "not"),
) {
    private fun unaryPrecedence(name: String): Int = when (name) {
        "+", "-" -> 9
        "not" -> 1
        else -> throw IllegalArgumentException("Unrecognized unary operator: $name")
    }

    private fun binaryPrecedence(name: String): Int = when (name) {
        "**" -> 8
        "*", "/", "%", "//" -> 7
        "+", "-" -> 6
        "<", "<=", ">", ">=" -> 5
        "==", "!=" -> 4
        "and" -> 3
        "or" -> 2
        else -> throw IllegalArgumentException("Unrecognized binary operator: $name")
    }

    private fun parsePrimary(tokenizer: Scanner<TokenType>): Evaluable =
        when (tokenizer.current.type) {
            TokenType.NUMBER ->
                Literal(tokenizer.consume().text.toDouble())
            TokenType.STRING -> {
                val text = tokenizer.consume().text
                Literal(
                    text.substring(1, text.length - 1)
                        .replace("\\n", "\n")
                )
            }
            TokenType.IDENTIFIER -> {
                var name = tokenizer.consume().text
                val children = if (tokenizer.tryConsume("(")) parseList(tokenizer, ")") else emptyList()
                Symbol(name, children)
            }
            TokenType.SYMBOL -> {
                if (!tokenizer.tryConsume("(")) {
                    throw tokenizer.exception("Unrecognized primary expression.")
                }
                val expr = parseExpression(tokenizer, Unit)
                tokenizer.consume(")")
                expr
            }
            else ->
                throw tokenizer.exception("Unrecognized primary expression.")
    }

    fun parseExpression(tokenizer: Scanner<TokenType>) = parseExpression(tokenizer, Unit)

    fun parseList(tokenizer: Scanner<TokenType>, endToken: String): List<Evaluable> {
        val parameters = mutableListOf<Evaluable>()
        if (tokenizer.current.text != endToken) {
                do {
                    parameters.add(parseExpression(tokenizer, Unit))
                } while (tokenizer.tryConsume(","))
            }
            tokenizer.consume(endToken)
        return parameters.toList()
    }

    fun eval(expression: String) = parseExpression(
        Scanner(ExpressionLexer(expression), TokenType.EOF)).eval(RootContext)
}
