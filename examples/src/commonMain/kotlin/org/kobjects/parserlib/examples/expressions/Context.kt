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


    fun eval(expr: String): Any? {
        val tokenizer = Tokenizer(expr)
        val parsed = ExpressionParser.parseExpression(tokenizer, this)
        if (!tokenizer.eof) {
            throw tokenizer.exception("End of input expected")
        }
        return if (parsed is Builtin && parsed.kind == Builtin.Kind.EQ && parsed.param[0] is Settable) {
            (parsed.param[0] as Settable).set(this, parsed.param[1])
        } else {
            parsed.eval(this)
        }
    }

}