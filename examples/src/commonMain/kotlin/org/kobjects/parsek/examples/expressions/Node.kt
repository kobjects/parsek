package org.kobjects.parsek.examples.expressions

interface Node {

    val name: String

    fun eval(ctx: RuntimeContext): Any

    fun evalDouble(ctx: RuntimeContext): Double = eval(ctx) as Double

    val children: List<Node>
        get() = emptyList()

}