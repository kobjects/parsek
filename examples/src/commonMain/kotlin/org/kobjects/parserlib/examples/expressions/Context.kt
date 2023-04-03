package org.kobjects.parserlib.examples.expressions

/**
 * Here, we use this class as parsing context and runtime context, but they could
 * be separate classes, in particular for compiled languages.
 */
open class Context {
    val variables = mutableMapOf<String, Any>()


    open fun resolveVariable(name: String): Evaluable = Variable(name)

    open fun resolveFunction(name: String, parameters: List<Evaluable>): Evaluable? {
        val n = (if (name.endsWith("_")) name.substring(0, name.length - 1) + "$" else name).uppercase()
        val builtin = Builtin.Kind.values().firstOrNull{it.name == n}
        return if (builtin == null) null
            else Builtin(builtin, *parameters.toTypedArray())
    }

}