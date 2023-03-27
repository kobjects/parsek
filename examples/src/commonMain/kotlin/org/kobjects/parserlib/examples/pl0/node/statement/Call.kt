package org.kobjects.parserlib.examples.pl0.node.statement

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext

data class Call(val name: String) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.call(name)
    }
    override fun toString(indent: String) = "CALL $name"
}