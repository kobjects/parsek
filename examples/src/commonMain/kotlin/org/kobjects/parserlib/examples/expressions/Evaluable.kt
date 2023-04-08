package org.kobjects.parserlib.examples.expressions

interface Evaluable {

    fun precedence(): Int = 10 // No parens required by default

    fun eval(ctx: Context): Any

    fun evalDouble(context: Context): Double {
        val value = eval(context)
        return if (value is Number) value.toDouble()
        else if (value is Boolean) {
            if (value) 1.0 else 0.0
        } else throw IllegalArgumentException("Number expected; got: 'value'")
    }

    fun evalInt(context: Context): Int = (eval(context) as Number).toInt()

    fun evalString(context: Context): String = eval(context) as String

    fun is0() = (this is Literal) && value is Number && value.toDouble() == 0.0

    fun is1() = (this is Literal) && value is Number && value.toDouble() == 1.0

    fun isBuiltin(kind: Builtin.Kind) =
        (this is Builtin) && this.kind == kind

    fun parenthesize(parentPrecedence: Int) =
        if (parentPrecedence > precedence()) "($this)"
        else toString()

    fun evalBoolean(context: Context): Boolean = evalDouble(context) != 0.0


}