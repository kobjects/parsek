package org.kobjects.parserlib.demos.cli.basic;

import org.kobjects.parserlib.examples.basic.Interpreter

fun main() {

    val interpreter = Interpreter()

    println("BASIC Interpreter")

    while (true) {
        print("> ")
        val s = readln()
        try {
            interpreter.processInputLine(s)
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}