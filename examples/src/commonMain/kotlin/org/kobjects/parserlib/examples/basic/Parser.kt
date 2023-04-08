package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.*

object Parser {


    fun parseStatement(tokenizer: Tokenizer, interpreter: Interpreter): Statement {
        var name = tokenizer.current.text
        if (tokenizer.tryConsume("GO", ignoreCase = true)) {  // GO TO, GO SUB -> GOTO, GOSUB
            name += tokenizer.current.text
        } else if (name == "?") {
            name = "PRINT"
        }
        var type: Statement.Kind? = null
        for (t in Statement.Kind.values()) {
            if (name.equals(t.name, ignoreCase = true)) {
                type = t
                break
            }
        }
        if (type == null) {
            type = Statement.Kind.LET
        } else {
            tokenizer.consume()
        }
        return when (type) {
            Statement.Kind.RUN,
            Statement.Kind.RESTORE -> {
                if (tokenizer.current.type != TokenType.EOF &&
                    tokenizer.current.text != ":"
                ) {
                    Statement(type, ExpressionParser.parseExpression(tokenizer, interpreter))
                } else Statement(type)
            }
            Statement.Kind.DEF,
            Statement.Kind.GOTO,
            Statement.Kind.GOSUB,
            Statement.Kind.LOAD -> Statement(
                type,
                ExpressionParser.parseExpression(tokenizer, interpreter))
            Statement.Kind.NEXT -> {
                val vars = mutableListOf<Evaluable>()
                if (tokenizer.current.type != TokenType.EOF &&
                    tokenizer.current.text != ":"
                ) {
                    do {
                        vars.add(ExpressionParser.parseExpression(tokenizer, interpreter))
                    } while (tokenizer.tryConsume(","))
                }
                Statement(type, *vars.toTypedArray())
            }
            Statement.Kind.DATA,
            Statement.Kind.DIM,
            Statement.Kind.READ -> {
                val expressions = mutableListOf<Evaluable>()
                do {
                    expressions.add(ExpressionParser.parseExpression(tokenizer, interpreter))
                } while (tokenizer.tryConsume(","))
                Statement(type, *expressions.toTypedArray())
            }
            Statement.Kind.FOR -> {
                val assignment = ExpressionParser.parseExpression(tokenizer, interpreter)
                if (assignment !is Builtin || assignment.kind != Builtin.Kind.EQ ||
                    assignment.param[0] !is Variable) {
                    throw tokenizer.exception("LocalVariable assignment expected after FOR")
                }
                tokenizer.consume( "TO")
                val end = ExpressionParser.parseExpression(tokenizer, interpreter)
                if (tokenizer.tryConsume( "STEP", ignoreCase = true)) {
                    Statement( type,
                        assignment.param[0],
                        assignment.param[1],
                        end,
                        ExpressionParser.parseExpression(tokenizer, interpreter),
                        delimiters = listOf(" = ", " TO ", " STEP ")
                    )
                } else Statement(
                    type,
                    assignment.param[0],
                    assignment.param[1],
                    end,
                    delimiters = listOf(" = ", " TO ", " STEP ")
                )
            }
            Statement.Kind.IF -> {
                val condition = ExpressionParser.parseExpression(tokenizer, interpreter)
                if (!tokenizer.tryConsume( "THEN", ignoreCase = true) && !tokenizer.tryConsume( "GOTO", ignoreCase = true)) {
                    throw tokenizer.exception("'THEN expected after IF-condition.'")
                }
                if (tokenizer.current.type === TokenType.NUMBER) {
                    val target = tokenizer.consume().text.toDouble()
                    return Statement(
                        type,
                        condition,
                        Literal(target),
                        delimiters = listOf(" THEN ")
                    )
                }
                Statement(type, condition, delimiters = listOf(" THEN"))
            }
            Statement.Kind.INPUT,
            Statement.Kind.PRINT -> {
                val args = mutableListOf<Evaluable>()
                val delimiter = mutableListOf<String>()
                while (tokenizer.current.type !== TokenType.EOF
                    && tokenizer.current.text != ":"
                ) {
                    if (tokenizer.current.text == "," || tokenizer.current.text == ";") {
                        delimiter.add(tokenizer.consume().text + " ")
                        if (delimiter.size > args.size) {
                            args.add(InvisibleStringLiteral)
                        }
                    } else {
                        args.add(ExpressionParser.parseExpression(tokenizer, interpreter))
                    }
                }
                Statement(type,
                    *args.toTypedArray(),
                    delimiters = delimiter
                )
            }
            Statement.Kind.LET -> {
                val assignment = ExpressionParser.parseExpression(tokenizer, interpreter)
                if (assignment !is Builtin || assignment.param[0] !is Settable
                    || assignment.kind != Builtin.Kind.EQ
                ) {
                    throw tokenizer.exception(
                        "Unrecognized statement or illegal assignment: '$assignment'.")

                }
                Statement(type, *assignment.param, delimiters = listOf(" = "))
            }
            Statement.Kind.ON -> {
                val expressions = mutableListOf<Evaluable>()
                expressions.add(ExpressionParser.parseExpression(tokenizer, interpreter))
                var kind: String
                if (tokenizer.tryConsume("GOTO", ignoreCase = true)) {
                    kind = " GOTO "
                } else if (tokenizer.tryConsume( "GOSUB", ignoreCase = true)) {
                    kind = " GOSUB "
                } else {
                    throw tokenizer.exception("GOTO or GOSUB expected.")
                }
                do {
                    expressions.add(ExpressionParser.parseExpression(tokenizer, interpreter))
                } while (tokenizer.tryConsume(","))
                Statement(type, *expressions.toTypedArray(), delimiters = listOf(kind))
            }
            Statement.Kind.REM -> {
                val sb = StringBuilder()
                while (tokenizer.current.type !== TokenType.EOF) {
                    sb.append(' ' /* tokenizer.leadingWhitespace */ )
                        .append(tokenizer.consume().text)
                    tokenizer.consume()
                }
                if (sb.isNotEmpty() && sb[0] == ' ') {
                    sb.deleteAt(0)
                }
                Statement(type, Variable(sb.toString()))
            }
            else -> Statement(type)
        }
    }

    fun parseStatementList(tokenizer: Tokenizer, interpreter: Interpreter): List<Statement> {
        val result = mutableListOf<Statement>()
        var statement: Statement
        do {
            while (tokenizer.tryConsume(":")) {
                result.add(Statement(Statement.Kind.EMPTY))
            }
            if (tokenizer.current.type == TokenType.EOF) {
                break
            }
            statement = parseStatement(tokenizer, interpreter)
            result.add(statement)
        } while (if (statement.kind === Statement.Kind.IF) statement.params.size == 1
            else tokenizer.tryConsume(":")
        )
        if (tokenizer.current.type !== TokenType.EOF) {
            throw tokenizer.exception("Leftover input.")
        }
        return result
    }
}