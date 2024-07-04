package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.RuntimeContext

data class Lambda(
    val parameters: List<String>,
    val body: Evaluable
) {
    fun eval(children: List<Evaluable>, callerContext: RuntimeContext): Any {
        val localContext = LocalContext(callerContext)
        for (i in parameters.indices) {
            localContext.symbols[parameters[i]] = children[i].eval(callerContext)
        }
        return body.eval(localContext)
    }

    override fun toString() =
        "(${parameters.joinToString (", ")}):\n  $body"
}