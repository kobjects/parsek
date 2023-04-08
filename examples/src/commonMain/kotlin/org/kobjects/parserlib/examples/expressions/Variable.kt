package org.kobjects.parserlib.examples.expressions

class Variable(
    val name: String,
) : Evaluable, Settable {

    override fun eval(ctx: Context) =
        ctx.variables.getOrElse(name) { if (name.endsWith("$")) "" else 0.0 }

    override fun set(ctx: Context, value: Any) {
        ctx.variables[name] = value
    }

    override fun toString() = name
}