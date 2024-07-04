package org.kobjects.parsek.examples.expressions


import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser
import org.kobjects.parsek.tokenizer.Scanner


object ExpressionParser : ConfigurableExpressionParser<Scanner<TokenType>, Unit, Evaluable>(
    { scanner, _ -> ExpressionParser.parsePrimary(scanner) },
    prefix(8, "+") { _, _, _, operand -> operand },
    prefix(8,  "-") { _, _, _, operand -> Symbol("neg", operand) },
    infix(7, "^") { _, _, _, left, right -> Symbol("pow", left, right) },
    infix(6, "*", "/") { _, _, name, left, right -> Symbol(if (name == "*") "mul" else "div", left, right) },
    infix(5, "+", "-") { _, _, name, left, right -> Symbol(if (name == "+") "add" else "sub", left, right) },
    infix(4, "<", "<=", ">", ">=") { _, _, name, left, right ->
        Symbol((if (name.startsWith("<")) "l" else "g") + (if (name.endsWith("=")) "e" else "t"), left, right) },
    infix(3, "==", "!=") { _, _, name, left, right -> Symbol(if (name == "==") "eq" else "ne", left, right) },
    infix(2, "and") { _, _, _, left, right -> Symbol("and", left, right) },
    infix(1, "or") { _, _, _, left, right -> Symbol("or", left, right) },
    prefix(0, "not") { _, _, _, operand -> Symbol("not", operand) }
) {
    private fun parsePrimary(tokenizer: Scanner<TokenType>): Evaluable =
        when (tokenizer.current.type) {
            TokenType.NUMBER ->
                Literal(tokenizer.consume().text.toDouble())
            TokenType.STRING -> {
                val text = tokenizer.consume().text
                Literal(
                    text.substring(1, text.length - 1)
                        .replace("\"\"", "\"")
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

    fun eval(expression: String) = parseExpression(ExpressionScanner(expression)).eval(RootContext)
}