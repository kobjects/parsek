package org.kobjects.parsek.examples.expressions

class Call(
    val name: String,
    val parameters: List<Evaluable>,
): Evaluable, Settable {
    val defaultValue: Any
        get() = if (name.endsWith("$")) "" else 0.0

    override fun eval(ctx: Context): Any {
        if (parameters.size > ctx.parameterized.size) {
            return defaultValue
        }

        val rootValue = ctx.parameterized[parameters.size].getOrElse(name) {
            return defaultValue
        }

        if (rootValue is FunctionDefinition) {
           return rootValue.eval(ctx, parameters)
        }

        var currentMap = rootValue as Map<Int, Any>

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
         while (parameters.size >= ctx.parameterized.size) {
            ctx.parameterized.add(mutableMapOf())
        }

        if (value is FunctionDefinition) {
            ctx.parameterized[parameters.size][name] = value
            return
        }

        var currentMap = ctx.parameterized[parameters.size].getOrPut(name) {
            mutableMapOf<Int, Any>()
        } as MutableMap<Int, Any>


        for (i in 0 until  parameters.size - 1) {
            currentMap = currentMap.getOrPut(parameters[i].evalInt(ctx)) {
                mutableMapOf<Int, Any>()
            } as MutableMap<Int, Any>
        }

        currentMap[parameters.last().evalInt(ctx)] = value
    }

    override fun toString() = name + "(" + parameters.joinToString(", ") + ")"
}