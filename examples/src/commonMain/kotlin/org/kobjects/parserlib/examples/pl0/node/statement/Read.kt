package org.kobjects.parserlib.examples.pl0.node.statement

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext

data class Read(val variable: String) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.set(variable, context.globalContext.read())
    }
    override fun toString(indent: String) = "? $variable"
}