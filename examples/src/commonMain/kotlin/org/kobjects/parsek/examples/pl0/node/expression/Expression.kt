package org.kobjects.parsek.examples.pl0.node.expression

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext

interface Expression {
    fun eval(context: EvaluationContext): Int
}