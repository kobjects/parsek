package org.kobjects.parserlib.demos.cli

import org.kobjects.parserlib.examples.spreadsheet.Spreadsheet

fun main() {
    println("ParserLib Spreadsheet Demo")
    println()
    println("Exit by entering 'exit'.")
    println("Command Examples:")
    println()
    println("a1 = \"Expenses\"")
    println("a2 = 7")
    println("a3 = 8")
    println("a4 = a2 + a3")
    println("list")
    println("")
    val spreadsheet = Spreadsheet()
    while (true) {
        print("cmd? ")
        val cmd = readln()
        if (cmd == "exit") {
            break
        }
        try {
            for (line in spreadsheet.process(cmd)) {
                println(line)
            }
        } catch (e: Exception) {
            println(e.toString())
        }

    }
}