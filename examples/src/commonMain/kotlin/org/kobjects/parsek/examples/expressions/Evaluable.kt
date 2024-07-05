package org.kobjects.parsek.examples.expressions

interface Evaluable {
    fun eval(context: RuntimeContext): Any

    fun evalDouble(context: RuntimeContext): Double = (eval(context) as Number).toDouble()
    fun evalBoolean(context: RuntimeContext): Boolean = eval(context) as Boolean
    fun evalLong(context: RuntimeContext): Long =
        when (val value = eval(context)) {
            is Long -> value
            is Number -> {
                require(value.toLong().toDouble() == value.toDouble()) { "Integer expected; got $value" }
                value.toLong()
            }
            else -> throw IllegalStateException("Integer expected; got $value")
        }

    fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        stringBuilder.append(this)
    }
}