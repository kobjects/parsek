package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.ExpressionParser
import org.kobjects.parsek.examples.expressions.ExpressionScanner
import org.kobjects.parsek.examples.expressions.Symbol
import org.kobjects.parsek.examples.expressions.TokenType

class MythonParser private constructor(val scanner: ExpressionScanner) {
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
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'def'."}.text
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
            result.add(parseStatement(depth))
            if (currentIndent() != depth) {
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
        return if (result.size == 1) result.first() else Symbol("seq", result)
    }

    fun parseStatement(depth: Int): Evaluable =
        if (scanner.tryConsume("if")) parseIf(depth)
        else if (scanner.tryConsume("while")) parseWhile(depth)
        else  ExpressionParser.parseExpression(scanner)

    fun parseIf(depth: Int): Evaluable {
        val condition = ExpressionParser.parseExpression(scanner)
        scanner.consume(":")
        val body = parseBody(depth)
        return Symbol("if", condition, body)
    }

    fun parseWhile(depth: Int): Evaluable {
        val condition = ExpressionParser.parseExpression(scanner)
        scanner.consume(":")
        val body = parseBody(depth)
        return Symbol("while", condition, body)
    }


    companion object {

        fun parseProgram(code: String) =
            MythonParser(ExpressionScanner(code)).parseProgram()
    }
}