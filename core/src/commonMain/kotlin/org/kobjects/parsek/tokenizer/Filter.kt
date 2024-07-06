package org.kobjects.parsek.tokenizer

/**
 * A Filter for the lexer, useful for filtering (e.g.) insignificant newlines. The accept function
 * is not just a parameter but needs to be overriden in a subclass as it's expected to be stateful.
 */
abstract class Filter<T>(val filtered: Iterator<Token<T>>) : Iterator<Token<T>> {
    private var accepted: Token<T>? = null

    abstract fun accept(token: Token<T>): Boolean

    override fun hasNext(): Boolean {
        while (accepted == null && filtered.hasNext()) {
            val canditate = filtered.next()
            if (accept(canditate)) {
                accepted = canditate
            }
        }
        return accepted != null
    }

    override fun next(): Token<T> {
        require (hasNext()) { "End of stream reached." }
        val result = accepted!!
        accepted = null
        return result
    }
}