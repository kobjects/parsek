package org.kobjects.parserlib.examples.pl0.node

import org.kobjects.parserlib.examples.pl0.EvaluationContext
import org.kobjects.parserlib.examples.pl0.GlobalContext

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
                mapOf()
            )
        )
    }

    override fun toString(): String = "$block."
}