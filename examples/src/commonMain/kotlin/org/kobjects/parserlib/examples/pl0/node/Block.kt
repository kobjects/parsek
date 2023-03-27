package org.kobjects.parserlib.examples.pl0.node

import org.kobjects.parserlib.examples.pl0.runtime.EvaluationContext
import org.kobjects.parserlib.examples.pl0.node.statement.Statement

data class Block(
    val symbols: Map<String, Int>,
    val procedures: Map<String, Block>,
    val statement: Statement
) {
    fun eval(context: EvaluationContext) {
        statement.eval(
            EvaluationContext(
                context.globalContext,
                context,
                symbols.toMutableMap(),
                procedures
            )
        )
    }

    override fun toString(): String = toString("")

    fun toString(indent: String): String {
        val sb = StringBuilder()
        // Note that This is incorrect if we define a 0 constant...
        if (symbols.any { it.value != 0 }) {
            sb.append(symbols
                .filter { it.value != 0 }
                .entries.map { "${it.key} = ${it.value}" }
                .joinToString(", ", "${indent}CONST ", ";\n"))
        }
        if (symbols.containsValue(0)) {
            sb.append(symbols.filter { it.value == 0 }.keys.joinToString(", ", "${indent}VAR ", ";\n"))
        }
        for (procedure in procedures.entries) {
            sb.append("${indent}PROCEDURE ${procedure.key}; ${procedure.value};\n")
        }
        sb.append(statement.toString(indent))
        return sb.toString()
    }
}