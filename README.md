# Parsek

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.kobjects.parsek/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.kobjects.parsek/core)

Parser library for Kotlin consisting of a tokenizer and expression parser.

## Tokenization

Tokenization is the process of splitting the input into a stream of token that is consumed by a parser.

In Parsek, this is distributed between two classes called Lexer and Scanner.


### Lexer

The lexer ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/Lexer.kt), 
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-lexer/)) is basically an iterator for 
a stream of tokens that is generated by splitting the input using regular expressions. 

Regular expressions are mapped to token types using a function which
typically just returns a fixed token type inline. The function can be used to implement a second
layer of mapping, but this should be fairly uncommon. Input mapped to null (typically whitespace) 
will not be reported.

The lexer is usually not used directly; instead, it's handed in to the Scanner,
which in turn is used by the parser. 

The reason for the Lexer/Scanner split is to separate "raw" parsing from providing a nice and convenient 
API. The small API surface of the Lexer allows us to easily install additional processing between the
Lexer and Scanner, for instance for context-sensitive newline filtering. 

Typically, the Lexer is constructed directly inline where the Scanner is constructed.


### Token

The token class ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/Token.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-token/)) stores the token type (typically a user-defined enum), the token text and the token position.
Token instances are generated by the Lexer.


### RegularExpressions

The RegularExpressions object ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/RegularExpressions.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-regular-expressions/)) contains a set 
of useful regular expressions for source code and data format tokenization.


### Scanner

The Scanner class  ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/Scanner.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-scanner/)) provides a 
simple API for convenient access to the token stream generated by the Lexer. 

- The scanner provides a notion of a "current" token that can be inspected multiple times -- opposed to
  iterator.next(), where the current token is "gone" after the call. This makes it easy to hand the
  scanner with the current token down in a recursive descend parser until it is consumed and processed 
  by the corresponding handler.

- It provides unlimited dynamic lookahead.

- It provides a tryConsume() convenience method that checks for a given token text and consumes the token
  and returns true when it was found.
  

### Scanner Use Cases

Typical use cases that only need a scanner and no expression parser are data formats such as JSON or CSV.

For a simple example, please refer to the [JSON parser example](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/json/).



## Expression Parser

The configurable expression parser  ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/expressionparser/ConfigurableExpressionParser.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.expressionparser/-configurable-expression-parser/)) 
operates on a tokenizer, is stateless and should be shared / reused.

- For ternary expressions, create a suffix expression and use the supplied tokenizer to consume the rest of the ternary.
- Functions / "Apply" can be implemented in a similar way. Alternatively, this can be implemented in primary expression 
- parsing by checking for an opening brace after the primary expression.
- "Grouping" brackets should be implemented where primary expressions are processed, too. 


### Expression Parser-Based Examples

 - A simple example evaluating mathematical expressions directly (opposed to building an explicit parse tree) can be 
   found in the [tests](https://github.com/kobjects/parsek/blob/main/core/src/commonTest/kotlin/org/kobjects/parsek/expressionparser/ParserTest.kt)

 - A complete [PL/0](https://en.wikipedia.org/wiki/PL/0) parser is included in the examples module to illustrate 
   how to use the expression parser and tokenizer for a simple but computational complete language: 
   [Parser.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/pl0/parser/Parser.kt),
   [Pl0Test.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonTest/kotlin/org/kobjects/parsek/examples/pl0/Pl0Test.kt)

- A parser for mathematical expressions: [ExpressionParser.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/expressions/ExpressionParser.kt), 
  [ExpressionsTest.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonTest/kotlin/org/kobjects/parsek/examples/expressions/ExpressionsTest.kt)

- A simple example for using the scanner and expression parser to implement a simple indentation-based
  programming language: [mython](https://github.com/kobjects/parsek/tree/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/mython), 
  [MythonTest.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonTest/kotlin/org/kobjects/parsek/examples/mython/MythonTest.kt)

- A BASIC interpreter using Parsek: https://github.com/stefanhaustein/basik