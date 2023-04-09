package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable
import org.kobjects.parserlib.examples.expressions.Settable

class ArrayVariable(
    val name: String,
    val parameters: List<Evaluable>,
): Evaluable, Settable {
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

        for (i in 0 until parameters.size - 1) {
            currentMap = currentMap.getOrElse(parameters[i].evalInt(ctx)) {
                return defaultValue
            } as MutableMap<Int, Any>
        }

        return currentMap.getOrElse(parameters.last().evalInt(ctx)) {
            defaultValue
        }
    }

    override fun set(ctx: Context, value: Any) {
        ctx as Interpreter

        while (parameters.size >= ctx.arrayVariables.size) {
            ctx.arrayVariables.add(mutableMapOf())
        }

        var currentMap = ctx.arrayVariables[parameters.size].getOrPut(name) {
            mutableMapOf()
        }

        for (i in 0 until  parameters.size - 1) {
            currentMap = currentMap.getOrPut(parameters[i].evalInt(ctx)) {
                mutableMapOf<Int, Any>()
            } as MutableMap<Int, Any>
        }

        currentMap[parameters.last().evalInt(ctx)] = value
    }
}