package org.kobjects.parserlib.examples.pl0.node.expression

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext

interface Expression {
    fun eval(context: EvaluationContext): Int
}