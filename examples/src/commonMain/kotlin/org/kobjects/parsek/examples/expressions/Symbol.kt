package org.kobjects.parsek.examples.expressions


class Symbol(
    val name: String,
    val children: List<Evaluable>
) : Evaluable {
    constructor(name: String, vararg children: Evaluable) : this(name, children.toList())
    override fun eval(context: RuntimeContext): Any = context.evalSymbol(name, children, context)
    override fun toString(): String = if (children.isEmpty()) name else "$name(${children.joinToString(", ")})"
}