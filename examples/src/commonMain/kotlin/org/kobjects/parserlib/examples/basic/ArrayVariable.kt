package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable

class ArrayVariable(
    val name: String,
    vararg val parameters: Evaluable,
): Evaluable {
    val defaultValue: Any
        get() = if (name.endsWith("$")) "" else 0.0

    override fun eval(ctx: Context): Any {
        ctx as Interpreter
        if (parameters.size > ctx.arrayVariables.size) {
            return defaultValue
        }

        var currentMap = ctx.arrayVariables[parameters.size].getOrElse(name) {
            return defaultValue
        }

        for (i in parameters.indices - 1) {
            currentMap = currentMap.getOrElse(parameters[i].evalInt(ctx)) {
                return defaultValue
            } as MutableMap<Int, Any>
        }

        return currentMap.getOrElse(parameters.last().evalInt(ctx)) {
            defaultValue
        }
    }
}