package org.kobjects.parserlib.examples.pl0.parser

import org.kobjects.parserlib.examples.pl0.node.*
import org.kobjects.parserlib.examples.pl0.node.condition.Condition
import org.kobjects.parserlib.examples.pl0.node.condition.Odd
import org.kobjects.parserlib.examples.pl0.node.condition.RelationalOperation
import org.kobjects.parserlib.examples.pl0.node.expression.*
import org.kobjects.parserlib.examples.pl0.node.statement.*
import org.kobjects.parserlib.expressionparser.ConfigurableExpressionParser


// expression = [ "+"|"-"] term { ("+"|"-") term};
// term = factor {("*"|"/") factor};
object Pl0Parser : ConfigurableExpressionParser<Pl0Scanner, ParsingContext, Expression>(
  { scanner, context -> Pl0Parser.parseFactor(scanner, context) },
  prefix(0, "+") { _, _, _, operand -> operand },
  prefix(0, "-") { _, _, _, operand -> Negate(operand) },
  infix(1, "*", "/") { _, _, name, left, right -> BinaryOperation(name, left, right) },
  infix(2, "+", "-") { _, _, name, left, right -> BinaryOperation(name, left, right) },
) {

    fun parseProgram(text: String): Program =
        parseProgram(Pl0Scanner(text))

    // program = block "." .
    fun parseProgram(scanner: Pl0Scanner): Program {
        val result = Program(parseBlock(scanner, null))
        scanner.consume(".")
        require(scanner.eof)
        return result
    }

    // block =
//   [ "CONST" ident "=" number { "," ident "=" number } ";" ]
//   [ "VAR" ident { "," ident } ";" ]
//   { "PROCEDURE" ident ";" block ";" }
//   statement .
    fun parseBlock(scanner: Pl0Scanner, parentContext: ParsingContext?): Block {
        val symbols = mutableMapOf<String, Int?>()  // We use null as value for variables here.
        if (scanner.tryConsume("CONST")) {
            do {
                val name = scanner.consume(TokenType.IDENT).text
                scanner.consume("=")
                val value = scanner.consume(TokenType.NUMBER).text.toInt()
                if (symbols.containsKey(name)) {
                    throw scanner.exception("Constant $name already defined.")
                }
                symbols[name] = value
            } while (scanner.tryConsume(","))
            scanner.consume(";")
        }
        if (scanner.tryConsume("VAR")) {
            do {
                val name = scanner.consume(TokenType.IDENT).text
                if (symbols.containsKey(name)) {
                    throw scanner.exception("Duplicate symbol '$name'")
                }
                symbols[name] = null
            } while (scanner.tryConsume(","))
            scanner.consume(";")
        }
        val procedures = mutableMapOf<String, Block>()
        val procedureNames = mutableSetOf<String>()
        while (scanner.tryConsume("PROCEDURE")) {
            val name = scanner.consume(TokenType.IDENT).text
            scanner.consume(";")
            if (procedureNames.contains(name)) {
                scanner.exception("Duplicate procedure name $name")
            }
            procedureNames.add(name)  // Permit recursion
            val block = parseBlock(scanner, ParsingContext(parentContext, symbols, procedureNames))
            scanner.consume(";")
            procedures.put(name, block);
        }
        val statement =
            parseStatement(scanner, ParsingContext(parentContext, symbols, procedureNames))

        // The parser checks that constants are not overwritten, so we don't need the distinction
        // any longer and replaces nulls with 0.
        return Block(symbols.mapValues { it.value ?: 0 }, procedures, statement)
    }

    // statement = [ ident ":=" expression
//   | "CALL" ident
//   | "?" ident
//   | "!" expression
//   | "BEGIN" statement {";" statement } "END"
//   | "IF" condition "THEN" statement
//   | "WHILE" condition "DO" statement ];
    fun parseStatement(scanner: Pl0Scanner, context: ParsingContext): Statement =
        if (scanner.current.type == TokenType.IDENT) {
            val variable = scanner.consume(TokenType.IDENT).text
            scanner.consume(":=")
            Assignment(variable, parseExpression(scanner, context))
        } else if (scanner.tryConsume("CALL")) {
            val name = scanner.consume(TokenType.IDENT).text
            if (!context.procedureNames.contains(name)) {
                throw scanner.exception("Undefined procedure $name")
            }
            Call(name)
        } else if (scanner.tryConsume("?")) {
            val variable = scanner.consume(TokenType.IDENT).text
            if (!context.symbols.containsKey(variable)) {
                throw scanner.exception("Undefined variable $variable")
            }
            if (context.symbols[variable] != null) {
                throw scanner.exception("Can't read constant $variable")
            }
            Read(variable)
        } else if (scanner.tryConsume("!")) {
            Write(parseExpression(scanner, context))
        } else if (scanner.tryConsume("BEGIN")) {
            val statements = mutableListOf<Statement>()
            do {
                statements.add(parseStatement(scanner, context))
            } while (scanner.tryConsume(";"))
            scanner.consume("END")
            BeginEnd(statements)
        } else if (scanner.tryConsume("IF")) {
            val condition = parseCondition(scanner, context)
            scanner.consume("THEN")
            If(condition, parseStatement(scanner, context))
        } else if (scanner.tryConsume("WHILE")) {
            val condition = parseCondition(scanner, context)
            scanner.consume("DO")
            While(condition, parseStatement(scanner, context))
        } else {
            EmptyStatement()
        }

    // condition = "ODD" expression |
//             expression ("="|"#"|"<"|"<="|">"|">=") expression ;
    fun parseCondition(scanner: Pl0Scanner, context: ParsingContext): Condition {
        if (scanner.tryConsume("ODD")) {
            return Odd(parseExpression(scanner, context));
        }
        val left = parseExpression(scanner, context)
        val name = scanner.consume(TokenType.COMPARISON).text
        return RelationalOperation(name, left, parseExpression(scanner, context))
    }

    /*
    // Implemented using the expression parser to reduce code size (also to avoid
// building a right-hanging tree without extra complexity)
    fun parseExpression(scanner: Pl0Scanner, context: ParsingContext) =
        parse(scanner, context)
*/

    // factor = ident | number | "(" expression ")";
    fun parseFactor(scanner: Pl0Scanner, context: ParsingContext): Expression =
        when (scanner.current.type) {
            TokenType.NUMBER ->
                Number(scanner.consume(TokenType.NUMBER).text.toInt())
            TokenType.IDENT ->
                Symbol(
                    scanner.consume(
                        TokenType.IDENT
                    ).text
                )
            else -> {
                scanner.consume("(")
                val result = parseExpression(scanner, context)
                scanner.consume(")")
                result
            }
        }

}
