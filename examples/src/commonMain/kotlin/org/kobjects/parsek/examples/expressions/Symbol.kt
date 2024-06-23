package org.kobjects.parsek.examples.expressions


class Symbol(
    override val name: String,
    override val children: List<Node>
) : Node {
    constructor(name: String, vararg children: Node) : this(name, children.toList())

    override fun eval(context: RuntimeContext): Any = context.evalSymbol(name, children)
}