# Parsek

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.kobjects.parsek/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.kobjects.parsek/core)

Parser library for Kotlin consisting of a tokenizer and expression parser.

## Tokenization

Lexer and Scanner instances are stateful and can't be reused.

The RegularExpressions class contains a set of useful regular expressions for parsing "C-Style" code.

Simple usage examples can be found in the [ScannerTest](https://github.com/kobjects/parsek/blob/main/core/src/commonTest/kotlin/org/kobjects/parsek/tokenizer/ScannerTest.kt)


## Expression Parser

The expression parser operates on a tokenizer, is stateless and should be shared / reused.

- For ternary expressions, create a suffix expression and use the supplied tokenizer to consume the rest of the ternary.
- Functions / "Apply" can be implemented in a similar way. Alternatively, this can be implemented in primary expression parsing by checking for
  an opening brace after the primary expression.
- "Grouping" brackets should be implemented where primary expressions are processed, too. 

Again, simple usage examples can be found in the [tests](https://github.com/kobjects/parsek/blob/main/core/src/commonTest/kotlin/org/kobjects/parsek/expressionparser/ParserTest.kt)

## Examples

### PL/0

A complete [PL/0](https://en.wikipedia.org/wiki/PL/0) parser is included in the examples module to 
[illustrate how to use the expression parser and tokenizer](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/pl0/Parser.kt) 
for a simple but fully working and computational complete language.
