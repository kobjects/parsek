package org.kobjects.parserlib.examples.expressions

class FunctionDefinition(val parameterNames: List<String>, val expression: Evaluable) {

    fun eval(ctx: Context, params: List<Evaluable>): Any {
        val saved = arrayOfNulls<Any>(parameterNames.size)
        for (i in parameterNames.indices) {
            val param = parameterNames[i]
            saved[i] = ctx.variables[param]
            ctx.variables[param] = params[i].eval(ctx)
        }
        return try {
            expression.eval(ctx)
        } finally {
            for (i in parameterNames.indices) {
                val restore = saved[i]
                val name = parameterNames[i]
                if (restore == null) {
                    ctx.variables.remove(name)
                } else {
                    ctx.variables[name] = restore
                }
            }
        }
    }
}