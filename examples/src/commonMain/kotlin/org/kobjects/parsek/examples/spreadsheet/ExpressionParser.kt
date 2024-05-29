package org.kobjects.parsek.examples.spreadsheet

import org.kobjects.parsek.examples.expressions.Context
import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.Tokenizer
import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser

object ExpressionParser : ConfigurableExpressionParser<Tokenizer, Context, Evaluable>(
    org.kobjects.parsek.examples.expressions.ExpressionParser.parsePrimary,
    *(org.kobjects.parsek.examples.expressions.ExpressionParser.configuration.toList()
            + listOf(infix(1, ":") { _, _, _, left, right -> RangeReference(left, right)})).toTypedArray()
) {
}