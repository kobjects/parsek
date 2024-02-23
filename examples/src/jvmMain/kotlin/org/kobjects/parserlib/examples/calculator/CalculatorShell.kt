package org.kobjects.parserlib.examples.calculator

import org.kobjects.parserlib.examples.calculator.Calculator

fun main() {
    println("ParserLib Calculator demo. Exit by entering a blank line.")
    while (true) {
        print("expression? ")
        val expression = readln()
        if (expression.isBlank()) {
            break
        }
        try {
            val result = Calculator.eval(expression)
            println("result: $result")
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}