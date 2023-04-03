package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable
import org.kobjects.parserlib.examples.expressions.Tokenizer
import org.kobjects.parserlib.expressionparser.ConfigurableExpressionParser

object ExpressionParser : ConfigurableExpressionParser<Tokenizer, Context, Evaluable>(
    org.kobjects.parserlib.examples.expressions.ExpressionParser.parsePrimary,
    *(org.kobjects.parserlib.examples.expressions.ExpressionParser.configuration.toList()
            + listOf(infix(1, ":") { _, _, _, left, right -> RangeReference(left, right)})).toTypedArray()
) {
}