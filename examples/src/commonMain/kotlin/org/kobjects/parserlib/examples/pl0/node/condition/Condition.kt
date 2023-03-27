package org.kobjects.parserlib.examples.pl0.node.condition

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext

interface Condition {
    fun eval(context: EvaluationContext): Boolean
}