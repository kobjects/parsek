package org.kobjects.parserlib.examples.pl0.node.statement

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext
import org.kobjects.parserlib.examples.pl0.node.expression.Expression

data class Write(val experession: Expression) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.globalContext.write(experession.eval(context))
    }
    override fun toString(indent: String) = "! $experession"
}