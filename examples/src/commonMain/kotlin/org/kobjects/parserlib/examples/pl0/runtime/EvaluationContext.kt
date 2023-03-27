package org.kobjects.parserlib.examples.pl0.runtime

import org.kobjects.parserlib.examples.pl0.node.Block

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