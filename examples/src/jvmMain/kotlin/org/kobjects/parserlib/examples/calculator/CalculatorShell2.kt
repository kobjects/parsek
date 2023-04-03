package org.kobjects.parserlib.examples.calculator

fun main() {
    while (true) {
        println("expression? ")
        val expression = readln()
        if (expression.isBlank()) {
            break
        }
        val result = Calculator.eval(expression)
        println("result: $result")
    }
}