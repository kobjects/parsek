package org.kobjects.parsek.examples.expressions

class Literal(val value: Any) : Evaluable {
    override fun eval(ctx: RuntimeContext) = value

    override fun toString() =
        if (value is String) "\"" + value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()
}