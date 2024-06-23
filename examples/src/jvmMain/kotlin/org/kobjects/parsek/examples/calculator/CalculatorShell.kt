package org.kobjects.parsek.examples.calculator

fun main() {
    println("Parsek Calculator demo. Exit by entering a blank line.")
    while (true) {
        print("expression? ")
        val expression = readln()
        if (expression.isBlank()) {
            break
        }
        try {
            val result = Calculator.calculate(expression)
            println("result: $result")
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}