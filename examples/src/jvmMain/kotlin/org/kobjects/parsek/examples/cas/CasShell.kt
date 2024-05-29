package org.kobjects.parsek.examples.cas

import org.kobjects.parsek.examples.algebra.Cas

fun main() {
    println("Parsek \"CAS\" demo. Exit by entering a blank line.")
    println()
    println("Try derive(x^2, x)")
    println()
    val cas = Cas()
    while (true) {
        print("expression? ")
        val expression = readln()
        if (expression.isBlank()) {
            break
        }
        try {
            val result = cas.process(expression)
            println("result: $result")
        } catch (e: Exception) {
            println(e)
        }
    }
}