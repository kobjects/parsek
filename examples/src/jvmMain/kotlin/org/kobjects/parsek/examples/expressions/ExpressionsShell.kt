package org.kobjects.parsek.examples.expressions


fun main() {
    println("Parsek \"Expressions\" demo. Exit by entering a blank line.")
    println()
    while (true) {
        print("expression? ")
        val expression = readln()
        if (expression.isBlank()) {
            break
        }
        try {
            val result = ExpressionParser.eval(expression)
            println("result: $result")
        } catch (e: Exception) {
            println(e)
        }
    }
}