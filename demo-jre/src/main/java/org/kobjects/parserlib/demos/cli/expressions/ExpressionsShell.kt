package org.kobjects.parserlib.demos.cli.expressions

import org.kobjects.parserlib.examples.expressions.Context


fun main() {
    println("ParserLib \"Expressions\" demo. Exit by entering a blank line.")
    println()
    val context = Context()
    while (true) {
        print("expression? ")
        val expression = readln()
        if (expression.isBlank()) {
            break
        }
        try {
            val result = context.eval(expression)
            println("result: $result")
        } catch (e: Exception) {
            println(e)
        }
    }
}