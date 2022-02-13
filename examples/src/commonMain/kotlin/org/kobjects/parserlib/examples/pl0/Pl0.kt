package org.kobjects.parserlib.examples.pl0

fun parseProgram(text: String): Program = parseProgram(Pl0Tokenizer(text))

/**
 * For information about pl/0, please refer to https://en.wikipedia.org/wiki/PL/0
 */
data class Program(val block: Block) {

    fun eval(
        write: (Int) -> Unit,
         read: () -> Int
    ) {
        block.eval(
            EvaluationContext(
                GlobalContext(write, read),
                null,
                mutableMapOf(),
                mapOf())
        )
    }

    override fun toString(): String = "$block."
}

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
                procedures)
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

class GlobalContext(
    val write: (Int) -> Unit,
    val read: () -> Int
)

class EvaluationContext(
    val globalContext: GlobalContext,
    val parent: EvaluationContext?,
    val symbols: MutableMap<String, Int>,
    val procedures: Map<String, Block>
) {
    fun get(name: String): Int = symbols[name] ?: parent!!.get(name)
    fun set(name: String, value: Int) {
        if (symbols.containsKey(name)) {
            symbols.put(name, value)
        } else {
            parent!!.set(name, value)
        }
    }
    fun call(name: String) {
        // We need to call from the right context to avoid potentially wrong shadowing effects.
        val procedure = procedures[name]
        if (procedure == null) {
            parent!!.call(name)
        } else {
            procedure.eval(this)
        }
    }
}
