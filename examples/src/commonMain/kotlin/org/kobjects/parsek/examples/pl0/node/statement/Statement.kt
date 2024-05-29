package org.kobjects.parsek.examples.pl0.node.statement

import org.kobjects.parsek.examples.pl0.runtime.EvaluationContext

abstract class Statement {
    abstract fun eval(context: EvaluationContext)
    abstract fun toString(indent: String): String
    override fun toString() = toString("")
}