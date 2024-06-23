package org.kobjects.parsek.examples.expressions

class Literal(val value: Any) : Node {

    override val name: String
        get() = if (value is String) "\"" + value.replace("\"", "\"\"") + "\""
                else value.toString()

    override fun eval(ctx: RuntimeContext) = value

    override fun toString() = name
}