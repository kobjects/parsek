package org.kobjects.parserlib.examples.calculator

fun main() {
    println("ParserLib Calculator demo. Exit by entering a blank line.")
    while (true) {
        print("expression? ")
        val expression = readln()
        if (expression.isBlank()) {
            break
        }
        val result = Calculator.eval(expression)
        println("result: $result")
    }
}