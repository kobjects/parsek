package org.kobjects.parserlib.examples.pl0

import org.kobjects.parserlib.expressionparser.ExpressionParser
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Tokenizer

fun parseProgram(text: String): Program = parseProgram(Pl0Tokenizer(text))

// program = block "." .
fun parseProgram(tokenizer: Pl0Tokenizer): Program {
    tokenizer.consume(TokenType.BOF)
    val result = Program(parseBlock(tokenizer, null))
    tokenizer.consume(".")
    tokenizer.consume(TokenType.EOF)
    return result
}

// block =
//   [ "CONST" ident "=" number { "," ident "=" number } ";" ]
//   [ "VAR" ident { "," ident } ";" ]
//   { "PROCEDURE" ident ";" block ";" }
//   statement .
fun parseBlock(tokenizer: Pl0Tokenizer, parentContext: ParsingContext?): Block {
    val symbols = mutableMapOf<String, Int?>()  // We use null as value for variables here.
    if (tokenizer.tryConsume("CONST")) {
        do {
            val name = tokenizer.consume(TokenType.IDENT)
            tokenizer.consume("=")
            val value = tokenizer.consume(TokenType.NUMBER).toInt()
            if (symbols.containsKey(name)) {
                throw tokenizer.error("Constant $name already defined.")
            }
            symbols[name] = value
        } while (tokenizer.tryConsume(","))
        tokenizer.consume(";")
    }
    if (tokenizer.tryConsume("VAR")) {
        do {
            val name = tokenizer.consume(TokenType.IDENT)
            if (symbols.containsKey(name)) {
                throw tokenizer.error("Duplicate symbol $name")
            }
            symbols[name] = null
        } while (tokenizer.tryConsume(","))
        tokenizer.consume(";")
    }
    val procedures = mutableMapOf<String, Block>()
    val procedureNames = mutableSetOf<String>()
    while (tokenizer.tryConsume("PROCEDURE")) {
        val name = tokenizer.consume(TokenType.IDENT)
        tokenizer.consume(";")
        if (procedureNames.contains(name)) {
            tokenizer.error("Duplicate procedure name $name")
        }
        procedureNames.add(name)  // Permit recursion
        val block = parseBlock(tokenizer, ParsingContext(parentContext, symbols, procedureNames))
        tokenizer.consume(";")
        procedures.put(name, block);
    }
    val statement = parseStatement(tokenizer, ParsingContext(parentContext, symbols, procedureNames))

    // The parser checks that constants are not overwritten, so we don't need the distinction
    // any longer and replaces nulls with 0.
    return Block(symbols.mapValues {  it.value ?: 0 }, procedures, statement)
}

// statement = [
//   | "CALL" ident
//   | "?" ident
//   | "!" expression
//   | "BEGIN" statement {";" statement } "END"
//   | "IF" condition "THEN" statement
//   | "WHILE" condition "DO" statement
//   | ident ":=" expression ];
fun parseStatement(tokenizer: Pl0Tokenizer, context: ParsingContext): Statement {
    val result: Statement
    if (tokenizer.tryConsume("CALL")) {
        val name = tokenizer.consume(TokenType.IDENT)
        if (!context.procedureNames.contains(name)) {
            throw tokenizer.error("Undefined procedure $name")
        }
        result = Call(name)
    } else if (tokenizer.tryConsume("?")) {
        val variable = tokenizer.consume(TokenType.IDENT)
        if (!context.symbols.containsKey(variable)) {
            throw tokenizer.error("Undefined variable $variable")
        }
        if (context.symbols[variable] != null) {
            throw tokenizer.error("Can't read constant $variable")
        }
        result = Read(variable)
    } else if (tokenizer.tryConsume("!")) {
        result = Write(parseExpression(tokenizer, context))
    } else if (tokenizer.tryConsume("BEGIN")) {
        val statements = mutableListOf<Statement>()
        do {
            statements.add(parseStatement(tokenizer, context))
        } while (tokenizer.tryConsume(";"))
        tokenizer.consume("END")
        return BeginEnd(statements)
    } else if (tokenizer.tryConsume("IF")) {
        val condition = parseCondition(tokenizer, context)
        tokenizer.consume("THEN")
        result = If(condition, parseStatement(tokenizer, context))
    } else if (tokenizer.tryConsume("WHILE")) {
        val condition = parseCondition(tokenizer, context)
        tokenizer.consume("DO")
        result = While(condition, parseStatement(tokenizer, context))
    } else if (tokenizer.current.value == "END"
        || tokenizer.current.value == ";"
        || tokenizer.current.value == ".") {
        // Hack to detect empty statements without lookahead for ":="
        result = EmptyStatement()
    } else {
        // Has to be an Assignment if we get here...
        val variable = tokenizer.consume(TokenType.IDENT)
        tokenizer.consume(":=")
        result = Assignment(variable, parseExpression(tokenizer, context))
    }
    return result
}

// condition = "ODD" expression |
//             expression ("="|"#"|"<"|"<="|">"|">=") expression ;
fun parseCondition(tokenizer: Pl0Tokenizer, context: ParsingContext) : Condition {
    if (tokenizer.tryConsume("ODD")) {
        return Odd(parseExpression(tokenizer, context));
    }
    val left = parseExpression(tokenizer, context)
    val name = tokenizer.consume(TokenType.COMPARISON)
    return RelationalOperation(name, left, parseExpression(tokenizer, context))
}

// Implemented using the expression parser to reduce code size (also to avoid
// building a right-hanging tree without extra complexity)
fun parseExpression(tokenizer: Pl0Tokenizer, context: ParsingContext) =
    expressionParser.parse(tokenizer, context)

// factor = ident | number | "(" expression ")";
fun parseFactor(tokenizer: Pl0Tokenizer, context: ParsingContext): Expression =
    when (tokenizer.current.type) {
        TokenType.NUMBER ->
            Number(tokenizer.consume(TokenType.NUMBER).toInt())
        TokenType.IDENT ->
            Symbol(tokenizer.consume(TokenType.IDENT))
        else -> {
            tokenizer.consume("(")
            val result = parseExpression(tokenizer, context)
            tokenizer.consume(")")
            result
        }
    }

// expression = [ "+"|"-"] term { ("+"|"-") term};
// term = factor {("*"|"/") factor};
val expressionParser = ExpressionParser<Pl0Tokenizer, ParsingContext, Expression>(
    ExpressionParser.prefix(0, "+") { _, _, _, operand -> operand },
    ExpressionParser.prefix(0, "-") { _, _, _, operand -> Negate(operand) },
    ExpressionParser.infix(1, "*", "/") { _, _, name, left, right ->
        BinaryOperation(name, left, right) },
    ExpressionParser.infix(2, "+", "-") { _, _, name, left, right ->
        BinaryOperation(name, left, right) },
) { tokenizer, context -> parseFactor(tokenizer, context) }

/**
 * "symbols" contanins constants (mapped to an int) and variables (mapped to null)
 */
class ParsingContext(
    parentContext: ParsingContext?,
    symbols: Map<String, Int?>,
    procedureNames: Set<String>
) {
    val symbols: Map<String, Int?> = if (parentContext == null) symbols
    else parentContext.symbols.toMutableMap().apply { putAll( symbols) }.toMap()
    val procedureNames: Set<String> = if (parentContext == null) procedureNames
    else parentContext.procedureNames.toMutableSet().apply { addAll (procedureNames)}
}

enum class TokenType {
    BOF, IDENT, NUMBER, COMPARISON, SYMBOL, EOF
}

class Pl0Tokenizer(input: String) : Tokenizer<TokenType>(
    TokenType.BOF,
    listOf(
        RegularExpressions.WHITESPACE to null,
        Regex("[0-9]+") to TokenType.NUMBER,
        Regex("[a-zA-Z]+") to TokenType.IDENT,
        Regex("<=|>=|=|<|>|#") to TokenType.COMPARISON,
        Regex("\\(|\\)|:=|;|\\.|!|\\?|\\+|-|\\*|/") to TokenType.SYMBOL
    ),
    TokenType.EOF,
    input
)
