package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.ExpressionParser
import org.kobjects.parsek.examples.expressions.ExpressionLexer
import org.kobjects.parsek.examples.expressions.Literal
import org.kobjects.parsek.examples.expressions.Symbol
import org.kobjects.parsek.examples.expressions.TokenType
import org.kobjects.parsek.tokenizer.Scanner

class MythonParser private constructor(val scanner: Scanner<TokenType>) {
    val functions = mutableMapOf<String, Lambda>()

    private fun parseExpression(): Evaluable = ExpressionParser.parseExpression(scanner)

    private fun currentIndent(): Int {
        if (scanner.current.type == TokenType.EOF) {
            return 0
        }
        scanner.require(scanner.current.type == TokenType.NEWLINE) { "Newline expected." }
        val newlinePos = scanner.current.text.lastIndexOf('\n')
        return scanner.current.text.length - newlinePos - 1
    }

    private fun parseProgram(): Program {
        while (scanner.current.type != TokenType.EOF) {
            if (scanner.current.type == TokenType.NEWLINE) {
                scanner.require(currentIndent() == 0) { "Unexpected indent: ${currentIndent()}." }
                scanner.consume()
            } else {
                when (scanner.current.text) {
                    "def" -> parserDef()
                    else -> throw scanner.exception("Unexpected token.")
                }
            }
        }
        require(functions.containsKey("main")) {
            "main() function not found."
        }
        return Program(functions.toMap())
    }

    fun parserDef() {
        scanner.consume("def")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'def'." }.text
        scanner.consume("(") { "Opening brace expected after function name '$name'." }
        val parameters = mutableListOf<String>()
        if (!scanner.tryConsume(")")) {
            do {
                parameters.add(scanner.consume(TokenType.IDENTIFIER) { "Parameter name expected." }.text)
            } while (scanner.tryConsume(","))
            scanner.consume(")") { "Closing brace or comma (')' or ',') expected after parameter" }
        }
        scanner.consume(":") { "Colon expected after function parameter list." }
        val body = parseBody(0)
        val fn = Lambda(parameters, body)
        functions[name] = fn
    }

    fun parseBody(parentDepth: Int): Evaluable {
        val depth = currentIndent()
        if (depth <= parentDepth) {
            return Symbol("seq")
        }
        scanner.consume(TokenType.NEWLINE)
        val result = mutableListOf<Evaluable>()
        while(true) {
            if (scanner.current.type != TokenType.NEWLINE) {
                val statement = parseStatement(depth)
                println("parsed: $statement")
                result.add(statement)
            }
            if (currentIndent() != depth) {
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
        return if (result.size == 1) result.first() else Symbol("seq", result)
    }

    fun parseStatement(depth: Int): Evaluable =
        if (scanner.tryConsume("for")) parseFor(depth)
        else if (scanner.tryConsume("if")) parseIf(depth)
        else if (scanner.tryConsume("while")) parseWhile(depth)
        else {
            val expr = ExpressionParser.parseExpression(scanner)
            if (scanner.tryConsume("=")) Symbol("=", expr, parseExpression()) else expr
        }

    fun parseFor(depth: Int): Evaluable {
        val varName = scanner.consume(TokenType.IDENTIFIER) { "Variable name expected." }.text
        scanner.consume("in") { "'in' expected after for loop variable."}
        val expr = parseExpression()
        scanner.consume(":") { "':' expected after for loop expression." }
        val body = parseBody(depth)
        return Symbol("for", Literal(varName), expr, body)
    }

    fun parseIf(depth: Int): Evaluable {
        val params = mutableListOf(ExpressionParser.parseExpression(scanner))
        scanner.consume(":")
        params.add(parseBody(depth))
        while (currentIndent() == depth && scanner.lookAhead(1).text == "elif") {
            scanner.consume()
            scanner.consume("elif")
            params.add(parseExpression())
            scanner.consume(":")
            params.add(parseBody(depth))
        }
        if (currentIndent() == depth && scanner.lookAhead(1).text == "else") {
            scanner.consume()
            scanner.consume("else")
            scanner.consume(":")
            params.add(parseBody(depth))
        }
        return Symbol("if", params.toList())
    }

    fun parseWhile(depth: Int): Evaluable {
        val condition = ExpressionParser.parseExpression(scanner)
        scanner.consume(":")
        val body = parseBody(depth)
        return Symbol("while", condition, body)
    }


    companion object {

        fun parseProgram(code: String) =
            MythonParser(Scanner(NewlineFilter(ExpressionLexer(code)), TokenType.EOF)).parseProgram()
    }
}