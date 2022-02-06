# parserlib

Kotlin library for building parsers, consisting mainly of a tokenizer and expression parser.


## Tokenizer

The tokenizer is stateful and can't be reused 

The RegularExpressions class contains a set of useful regular expressions for parsing "C-Style" code.

Simple usage examples can be found in the [TokenizerTest](shared/src/commonTest/kotlin/org/kobjects/parserlib/tokenizer/TokenizerTest.kt)


## Expression Parser

The expression parser operates on a tokenizer, is stateless and should be shared / reused.

- For ternary expressions, create a suffix expression and use the supplied tokenizer to consume the rest of the ternary.
- Functions / "Apply" can be implemented in a similar way. Alternatively, this can be implemented in primary expression parsing by checking for
  an opening brace after the primary expression.
- "Grouping" brackets should be implemented where primary expressions are processed, too. 

Again, simple usage examples can be found in the [tests](shared/src/commonTest/kotlin/org/kobjects/parserlib/expressionparser/ParserTest.kt)
