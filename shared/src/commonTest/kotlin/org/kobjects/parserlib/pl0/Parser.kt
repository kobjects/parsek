package org.kobjects.parserlib.pl0

import org.kobjects.parserlib.expressionparser.ExpressionParser
import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Tokenizer

// program = block "." .
fun parseProgram(tokenizer: Pl0Tokenizer): Program {
    tokenizer.consume(TokenType.BOF)
    val result = Program(parseBlock(tokenizer, null))
    tokenizer.consume(".")
    tokenizer.consume(TokenType.EOF)
    return result
}

// block      =
fun parseBlock(tokenizer: Pl0Tokenizer, parentContext: ParsingContext?): Block {
    // [ "CONST" ident "=" number { "," ident "=" number } ";" ]
    val symbols = mutableMapOf<String, Int?>()
    if (tokenizer.tryConsume("CONST")) {
        do {
            val name = tokenizer.consume(TokenType.IDENTIFIER)
            tokenizer.consume("=")
            val value = tokenizer.consume(TokenType.NUMBER).toInt()
            if (symbols.containsKey(name)) {
                throw tokenizer.error("Constant $name already defined.")
            }
            symbols[name] = value
        } while (tokenizer.tryConsume(","))
        tokenizer.consume(";")
    }

    // [ "VAR" ident { "," ident } ";" ]
    if (tokenizer.tryConsume("VAR")) {
        do {
            val name = tokenizer.consume(TokenType.IDENTIFIER)
            if (symbols.containsKey(name)) {
                throw tokenizer.error("Duplicate symbol $name")
            }
            symbols[name] = null
        } while (tokenizer.tryConsume(","))
        tokenizer.consume(";")
    }

    // { "PROCEDURE" ident ";" block ";" } statement .
    val procedures = mutableMapOf<String, Block>()
    val procedureNames = mutableSetOf<String>()
    while (tokenizer.tryConsume("PROCEDURE")) {
        val name = tokenizer.consume(TokenType.IDENTIFIER)
        tokenizer.consume(";")
        if (procedureNames.contains(name)) {
            tokenizer.error("Duplicate procedure name $name")
        }
        procedureNames.add(name)
        val block = parseBlock(tokenizer, ParsingContext(parentContext, symbols, procedureNames))
        tokenizer.consume(";")
        procedures.put(name, block);
    }
    val statement = parseStatement(tokenizer, ParsingContext(parentContext, symbols, procedureNames))

    return Block(symbols.mapValues {  it.value ?: 0 }, procedures, statement)
}

fun parseStatement(tokenizer: Pl0Tokenizer, context: ParsingContext): Statement {
    if (tokenizer.tryConsume("CALL")) {
        val name = tokenizer.consume(TokenType.IDENTIFIER)
        if (!context.procedureNames.contains(name)) {
            throw tokenizer.error("Undefined procedure $name")
        }
        return Call(name)
    }
    if (tokenizer.tryConsume("?")) {
        val variable = tokenizer.consume(TokenType.IDENTIFIER)
        if (!context.symbols.containsKey(variable)) {
            throw tokenizer.error("Undefined variable $variable")
        }
        if (context.symbols[variable] != null) {
            throw tokenizer.error("Can't read constant $variable")
        }
        return Read(variable)
    }
    if (tokenizer.tryConsume("!")) {
        return Write(parseExpression(tokenizer, context))
    }
    if (tokenizer.tryConsume("BEGIN")) {
        val statements = mutableListOf<Statement>()
        do {
            statements.add(parseStatement(tokenizer, context))
        } while (tokenizer.tryConsume(";"))
        tokenizer.consume("END")
        return BeginEnd(statements)
    }
    if (tokenizer.tryConsume("IF")) {
        val condition = parseCondition(tokenizer, context)
        tokenizer.consume("THEN")
        return If(condition, parseStatement(tokenizer, context))
    }
    if (tokenizer.tryConsume("WHILE")) {
        val condition = parseCondition(tokenizer, context)
        tokenizer.consume("DO")
        return While(condition, parseStatement(tokenizer, context))
    }

    // Assignment
    val variable = tokenizer.consume(TokenType.IDENTIFIER)
    tokenizer.consume(":=")
    return Assignment(variable, parseExpression(tokenizer, context))
}

fun parseCondition(tokenizer: Pl0Tokenizer, context: ParsingContext) : Condition {
    if (tokenizer.tryConsume("ODD")) {
        return Odd(parseExpression(tokenizer, context));
    }
    val left = parseExpression(tokenizer, context)
    val name = tokenizer.consume(TokenType.COMPARISON)
    val right = parseExpression(tokenizer, context)
    val comparator : (Int, Int) -> Boolean = when (name) {
        "=" -> { l, r -> l == r}
        "#" -> { l, r -> l != r}
        "<" -> { l, r -> l < r}
        "<=" -> { l, r -> l <= r}
        ">" -> { l, r -> l > r}
        ">=" -> { l, r -> l >= r}
        else -> throw tokenizer.error("Unrecognized comparison $name")
    }
    return Comparison(name, left, right, comparator)
}

fun parseExpression(tokenizer: Pl0Tokenizer, context: ParsingContext) =
    expressionParser.parse(tokenizer, context)

fun parseFactor(tokenizer: Pl0Tokenizer, context: ParsingContext): Expression =
    when (tokenizer.current.type) {
        TokenType.NUMBER ->
            Number(tokenizer.consume(TokenType.NUMBER).toInt())
        TokenType.IDENTIFIER ->
            Symbol(tokenizer.consume(TokenType.IDENTIFIER))
        else -> {
            tokenizer.consume("(")
            val result = parseExpression(tokenizer, context)
            tokenizer.consume(")")
            result
        }
    }

enum class TokenType {
    BOF, IDENTIFIER, NUMBER, OPERATOR, COMPARISON, SYMBOL, EOF
}

class Pl0Tokenizer(input: String) : Tokenizer<TokenType>(
    TokenType.BOF,
    listOf(
        RegularExpressions.WHITESPACE to null,
        Regex("[0-9]+") to TokenType.NUMBER,
        Regex("[a-zA-Z]+") to TokenType.IDENTIFIER,
        Regex("\\+|-|\\*|/") to TokenType.OPERATOR,
                Regex("<=|>=|=|<|>|#") to TokenType.COMPARISON,
        Regex("\\(|\\)|:=|;|\\.|!|\\?") to TokenType.SYMBOL
    ),
    TokenType.EOF,
    input
)

/**
 * symbols contanins constants (mapped to an int) and variables (mapped to null)
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

val expressionParser = ExpressionParser<Pl0Tokenizer, ParsingContext, Expression>(
    ExpressionParser.prefix(0, "+") { _, _, _, operand -> operand },
    ExpressionParser.prefix(0, "-") { _, _, _, operand ->
        UnaryExpression("-", operand) { -it }
    },
    ExpressionParser.infix(1, "*") { _, _, _, left, right ->
        BinaryExpression("*", left, right) {l, r -> l * r}
    },
    ExpressionParser.infix(1, "/") { _, _, _, left, right ->
        BinaryExpression("/", left, right) {l, r -> l / r}
    },
    ExpressionParser.infix(2, "+") { _, _, _, left, right ->
        BinaryExpression("+", left, right) {l, r -> l + r}
    },
    ExpressionParser.infix(2, "-") { _, _, _, left, right ->
        BinaryExpression("-", left, right) {l, r -> l - r}
    },
) { tokenizer, context -> parseFactor(tokenizer, context) }