package org.kobjects.parserlib.examples.expressions


import org.kobjects.parserlib.expressionparser.ConfigurableExpressionParser

/**
 * Tailored towards the BASIC interpreter
 */
object ExpressionParser : ConfigurableExpressionParser<Tokenizer, Context, Evaluable>(
    { scanner, context -> ExpressionParser.parsePrimary(scanner, context) },
    prefix(Builtin.Kind.NEG.precedence, "+") { _, _, _, operand -> operand },
    prefix(Builtin.Kind.NEG.precedence,  "-") { _, _, _, operand -> Builtin(Builtin.Kind.NEG, operand) },
    infix(Builtin.Kind.POW.precedence, "^") { _, _, _, left, right ->
        Builtin(Builtin.Kind.POW, left, right) },
    infix(Builtin.Kind.MUL.precedence, "*", "/") { _, _, name, left, right ->
        Builtin(name, left, right) },
    infix(Builtin.Kind.ADD.precedence, "+", "-") { _, _, name, left, right ->
        Builtin(name, left, right) },
    infix(Builtin.Kind.EQ.precedence, "=", "<", "<=", "<>", ">", ">=") { _, _, name, left, right ->
        Builtin(name, left, right) },
    infix(Builtin.Kind.AND.precedence, "AND", "And", "and") { _, _, _, left, right ->
        Builtin(Builtin.Kind.AND, left, right) },
    infix(Builtin.Kind.OR.precedence, "OR", "Or", "or") { _, _, _, left, right ->
        Builtin(Builtin.Kind.OR, left, right) },
    prefix(Builtin.Kind.NOT.precedence, "NOT", "Not", "not") { _, _, _, operand ->
        Builtin(Builtin.Kind.NOT, operand) }

) {
    private fun parsePrimary(tokenizer: Tokenizer, context: Context): Evaluable =
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
                if (name.equals("FN", ignoreCase = true) && tokenizer.current.type == TokenType.IDENTIFIER) {
                    name += " " + tokenizer.consume().text
                }

                if (tokenizer.tryConsume("(")) {
                    val params = parseParameterList(tokenizer, context, ")")
                    context.resolveFunction(name, params)!!
                } else {
                    context.resolveVariable(name)
                }
            }
            TokenType.SYMBOL -> {
                if (!tokenizer.tryConsume("(")) {
                    throw tokenizer.exception("Unrecognized primary expression.")
                }
                val expr = parseExpression(tokenizer, context)
                tokenizer.consume(")")
                Builtin(Builtin.Kind.EMPTY, expr)
            }
            else ->
                throw tokenizer.exception("Unrecognized primary expression.")
    }


    fun parseParameterList(tokenizer: Tokenizer, context: Context, endToken: String): List<Evaluable> {
        val parameters = mutableListOf<Evaluable>()
        if (tokenizer.current.text != endToken) {
                do {
                    parameters.add(parseExpression(tokenizer, context))
                } while (tokenizer.tryConsume(","))
            }
            tokenizer.consume(endToken)
        return parameters
    }
}