package org.kobjects.parsek.examples.mython

import org.kobjects.parsek.examples.expressions.Literal

class Program(
    val functions: Map<String, Lambda>
)  {

    override fun toString() =
        buildString {
            for ((name, def) in functions) {
                append("def $name")
                append(def)
                append("\n")
            }
        }

    fun run(vararg parameters: Any, printFn: (String) -> Unit = { print(it) }): Any {

        return functions["main"]?.eval(parameters.map { Literal(it) }, ProgramContext(this, printFn)) ?: throw IllegalStateException("main function not found.")

    }
}