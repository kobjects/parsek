package org.kobjects.parsek.examples.pl0.node.condition

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext

interface Condition {
    fun eval(context: EvaluationContext): Boolean
}