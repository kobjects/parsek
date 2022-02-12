package org.kobjects.parserlib.examples.pl0


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
    fun set(name: String, value: Int) = symbols.put(name, value)
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
