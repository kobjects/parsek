package org.kobjects.parsek.examples.expressions


import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser
import org.kobjects.parsek.tokenizer.Scanner


object ExpressionParser : ConfigurableExpressionParser<Scanner<TokenType>, Unit, Evaluable>(
    { scanner, _ -> ExpressionParser.parsePrimary(scanner) },
    prefix(9, "+", "-") { _, _, name, operand -> Symbol(name, 9, operand) },
    infix(8, "**") { _, _, _, left, right -> Symbol("**", 8, left, right) },
    infix(7, "*", "/", "%", "//") { _, _, name, left, right -> Symbol(name, 7, left, right) },
    infix(6, "+", "-") { _, _, name, left, right -> Symbol(name, 6, left, right) },
    infix(5, "<", "<=", ">", ">=") { _, _, name, left, right -> Symbol(name, 5, left, right) },
    infix(4, "==", "!=") { _, _, name, left, right -> Symbol(name, 4, left, right) },
    infix(3, "and") { _, _, _, left, right -> Symbol("and", 3, left, right) },
    infix(2, "or") { _, _, _, left, right -> Symbol("or", 2, left, right) },
    prefix(1, "not") { _, _, _, operand -> Symbol("not", 1, operand) }
) {
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