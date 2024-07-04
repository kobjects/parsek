package org.kobjects.parsek.examples.expressions

interface Evaluable {
    fun eval(ctx: RuntimeContext): Any

    fun evalDouble(ctx: RuntimeContext): Double = eval(ctx) as Double
    fun evalBoolean(ctx: RuntimeContext): Boolean = eval(ctx) as Boolean
}