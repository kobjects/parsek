# parserlib

Kotlin library for building parsers, consisting mainly of a tokenizer and expression parser.


## Tokenizer

The tokenizer is stateful and can't be reused 

The RegularExpressions class contains a set of useful regular expressions for parsing "C-Style" code.

Simple usage examples can be found in the [TokenizerTest](shared/commonTest/kotlin/org/kobjects/parserlib/tokenizer/TokenizerTest.kt)


## Parser

The expression parser operates on a tokenizer, is stateless and should be shared / reused.

- For ternary expressions, please create a suffix expression and use the supplied tokenizer to consume the rest of the ternary.
- Functions / "Apply" can be implemented in a similar way. Alternatively, they can be implemented in processing primary expressions.
- "Grouping" brackets should be implemented where primary expressions are processed, too. 

Again, simple usage examples can be found in the [tests](shared/commonTest/kotlin/org/kobjects/parserlib/expressionparser/ParserTest.kt)
