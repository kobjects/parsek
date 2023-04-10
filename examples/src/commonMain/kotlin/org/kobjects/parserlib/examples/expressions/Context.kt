package org.kobjects.parserlib.examples.expressions

/**
 * Here, we use this class as parsing context and runtime context, but they could
 * be separate classes, in particular for compiled languages.
 */
open class Context {
    val variables = mutableMapOf<String, Any>()
    // The array index corresponds to the number of parameters.
    val parameterized = mutableListOf<MutableMap<String, Any>>()

    open fun resolveVariable(name: String): Evaluable = Variable(name.lowercase())

    open fun resolveFunction(name: String, parameters: List<Evaluable>): Evaluable {
        val builtin = Builtin.Kind.values().firstOrNull{it.toString().equals(name, ignoreCase = true)}
        return if (builtin == null) Call(name, parameters)
            else Builtin(builtin, *parameters.toTypedArray())
    }


    fun eval(expr: String): Any {
        val tokenizer = Tokenizer(expr)
        val def = tokenizer.tryConsume("def")
        val parsed = ExpressionParser.parseExpression(tokenizer, this)
        if (!tokenizer.eof) {
            throw tokenizer.exception("End of input expected")
        }
        return if (def) {
            val definition = FunctionDefinition(parsed)
            ((parsed as Builtin).param[0] as Settable).set(this, definition)
        } else if (parsed is Builtin && parsed.kind == Builtin.Kind.EQ && parsed.param[0] is Settable) {
            (parsed.param[0] as Settable).set(this, parsed.param[1])
        } else {
            parsed.eval(this)
        }
    }

}