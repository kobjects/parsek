package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable

class FnCall(
    val name: String,
    val params: List<Evaluable>,
) : Evaluable {
    override fun eval(ctx: Context): Any {
        ctx as Interpreter
        return ctx.functionDefinitions[name]!!.eval(ctx, params)
    }
}