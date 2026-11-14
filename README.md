# Parsek

[![Maven Central](https://img.shields.io/maven-central/v/org.kobjects.parsek/core)](https://central.sonatype.com/artifact/org.kobjects.parsek/core)

Parser library for Kotlin consisting of a tokenizer and expression parser.

## Tokenization

Tokenization is the process of splitting the input into a stream of tokens that is consumed by a parser.

In Parsek, most parsers only need to interact with `Scanner` (and optionally `RegularExpressions`); the underlying `Lexer` is exposed as a raw `Iterator<Token<T>>` for cases that need custom token-stream processing.


### Scanner

The Scanner class ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/Scanner.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-scanner/)) provides a convenient API over a token stream:

- It can be constructed directly from an input string and `Regex to tokenType` pairs (mapping skipped tokens such as whitespace to `null`), or over any `Iterator<Token<T>>`.
- Subclassing `Scanner` (e.g. `class Pl0Scanner(input: String) : Scanner<TokenType>(input, TokenType.EOF, ...)`) is recommended so parser methods can accept `Pl0Scanner` without repeating `<TokenType>`, and custom helper methods can be added if needed. Direct `Scanner(...)` instantiation is also fine for simple cases.
- Store custom `Regex` instances in constants (`private val` or `RegularExpressions`) rather than constructing them inside the `Scanner` constructor on each parse call.
- Passing a non-null `eofType` (e.g. `TokenType.EOF`) keeps `Token.type` non-nullable. To avoid adding an `EOF` entry to an enum, pass `null` with a nullable type parameter (`Scanner<TokenType?>(input, null, ...)`), or use `Scanner<Unit?>(input, null, ...)` (mapping emitted tokens to `Unit`) when you don't need token types at all.
- It maintains a `current` token that can be inspected multiple times (unlike `Iterator.next()`), unlimited dynamic `lookAhead(n)`, and `consume` / `tryConsume` / `consumeIgnoreCase` / `tryConsumeIgnoreCase` helpers.


### Scanner Use Cases

Typical use cases that only need a scanner and no expression parser are data formats such as JSON or CSV.

For a simple example, please refer to the [JSON parser example](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/json/).


### RegularExpressions

The RegularExpressions object ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/RegularExpressions.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-regular-expressions/)) contains precompiled regular expressions for common source code and data format tokens.


### Token

The token class ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/Token.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-token/)) stores the token type (typically a user-defined enum), the token text and the token position.


### Lexer

The lexer ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/tokenizer/Lexer.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.tokenizer/-lexer/)) implements `Iterator<Token<T>>`, splitting an input string into tokens using `Regex to tokenType` pairs (multiple regular expressions may map to the same token type, and rules mapped to `null` are skipped).

You only need to instantiate or subclass `Lexer` directly when installing additional processing between raw tokenization and `Scanner` — for instance via `lexer.asSequence().filter { ... }.iterator()` for context-sensitive newline filtering in indentation-based languages.


## Expression Parser

The Pratt expression parser ([source](https://github.com/kobjects/parsek/blob/main/core/src/commonMain/kotlin/org/kobjects/parsek/expression/PrattParser.kt),
[kdoc](https://kobjects.org/parsek/dokka/-parsek/org.kobjects.parsek.expression/-pratt-parser/))
operates on a scanner, is stateless and should be shared / reused.

- For ternary expressions, create a suffix expression and use the supplied scanner to consume the rest of the ternary.
- Functions / "Apply" can be implemented in a similar way. Alternatively, this can be implemented in primary expression parsing by checking for an opening brace after the primary expression.
- "Grouping" brackets should be implemented where primary expressions are processed, too.


### Expression Parser-Based Examples

- A simple example evaluating mathematical expressions directly (opposed to building an explicit parse tree) can be
  found in the [tests](https://github.com/kobjects/parsek/blob/main/core/src/commonTest/kotlin/org/kobjects/parsek/expression/ParserTest.kt)

- A complete [PL/0](https://en.wikipedia.org/wiki/PL/0) parser is included in the examples module to illustrate
  how to use the expression parser and scanner for a simple but computationally complete language:
  [Pl0Scanner.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/pl0/parser/Pl0Scanner.kt),
  [Pl0Parser.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/pl0/parser/Pl0Parser.kt),
  [Pl0Test.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonTest/kotlin/org/kobjects/parsek/examples/pl0/Pl0Test.kt)

- A parser for mathematical expressions:
  [ExpressionLexer.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/expressions/ExpressionLexer.kt),
  [ExpressionScanner.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/expressions/ExpressionScanner.kt),
  [ExpressionParser.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/expressions/ExpressionParser.kt),
  [ExpressionsTest.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonTest/kotlin/org/kobjects/parsek/examples/expressions/ExpressionsTest.kt)

- A simple example for using the lexer, `Sequence.filter`, scanner, and expression parser to implement an indentation-based
  programming language: [mython](https://github.com/kobjects/parsek/tree/main/examples/src/commonMain/kotlin/org/kobjects/parsek/examples/mython),
  [MythonTest.kt](https://github.com/kobjects/parsek/blob/main/examples/src/commonTest/kotlin/org/kobjects/parsek/examples/mython/MythonTest.kt)

- A BASIC interpreter using Parsek: https://github.com/stefanhaustein/basik


## Version History

### 0.30.0 (2026-11-14)
- Simplified `Lexer` (`Iterator<Token<T>>`) rules to `Pair<Regex, T?>` directly, removing the mapping lambda and `textNormalization`.
- Changed `Scanner` EOF token text to `"<EOF>"`, added `consumeIgnoreCase(...)` / `tryConsumeIgnoreCase(...)`, and deprecated `Filter`, 3-argument `Scanner(input, eofType, eofText)`, and `ignoreCase` boolean overloads.
- Removed `ConfigurableExpressionParser` (`org.kobjects.parsek.expressionparser`).
- Renamed `RegularExpressions.NUMBER` to `UNSIGNED_NUMBER` (with `NUMBER` deprecated for one release) and fixed `RegularExpressions.SYMBOL` escaping for JS/Wasm.
- Made `Operator` a `sealed class`, added `PrattParser<S, Unit, R>.parseExpression(scanner)`, and dropped empty Javadoc JARs.

### 0.20.0 / 0.21.0 (2026-11-14) *(failed experiment, superseded by 0.30.0)*
- Short-lived `Lexer` factory experiment that broke `Scanner` subclassing; do not use.

### 0.11.0 (2026-11-14)
- Deprecated `ConfigurableExpressionParser` in favor of `PrattParser`.
- Migrated publishing to Maven Central Portal and updated Gradle / Kotlin / Dokka toolchain.

### 0.10.0 (2025-01-11)
- Added `PrattParser` (`org.kobjects.parsek.expression`).
- Added `Scanner` convenience constructor accepting regular expression pairs directly.

### 0.9.0 (2024-07-10)
- Added `Filter` base class and expanded `RegularExpressions`.
- Added the `mython` indentation-based language example.

### 0.8.1 (2024-06-16)
- Enabled `wasmJs` target.

### 0.8.0 (2024-06-09)
- Package name cleanup (`org.kobjects.parsek`), Maven Central publishing support, and JSON parser example.
