package org.kobjects.parserlib.examples.expressions

class Variable(
    val name: String,
) : Evaluable {

    override fun eval(ctx: Context) =
        ctx.variables.getOrElse(name) { if (name.endsWith("$")) "" else 0.0 }

    override fun toString() = name
}