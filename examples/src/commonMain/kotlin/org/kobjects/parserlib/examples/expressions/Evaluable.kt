package org.kobjects.parserlib.examples.expressions

interface Evaluable {

    fun precedence(): Int = 0

    fun eval(ctx: Context): Any

    fun evalDouble(context: Context): Double = (eval(context) as Number).toDouble()

    fun evalInt(context: Context): Int = (eval(context) as Number).toInt()

    fun evalString(context: Context): String = eval(context) as String

    fun is0() = (this is Literal) && value is Number && value.toDouble() == 0.0

    fun is1() = (this is Literal) && value is Number && value.toDouble() == 1.0

    fun isBuiltin(kind: Builtin.Kind) =
        (this is Builtin) && this.kind == kind

    fun toString(parentPrecedence: Int) =
        if (parentPrecedence < precedence()) "($this)"
        else toString()


}