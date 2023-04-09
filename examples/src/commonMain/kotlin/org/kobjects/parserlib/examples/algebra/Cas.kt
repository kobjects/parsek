package org.kobjects.parserlib.examples.algebra

import org.kobjects.parserlib.examples.expressions.*

class Cas : Context() {

    override fun resolveFunction(name: String, params: List<Evaluable>): Evaluable =
        if (name == "derive") {
            require(params.size == 2) { throw IllegalArgumentException("Two parameters expected for derive (variable, expression)")}
            require(params[1] is Variable) { throw IllegalArgumentException("First parameter must be a variable.") }
            params[0].derive((params[1] as Variable).name)
        } else
            super.resolveFunction(name, params)


    fun process(expression: String) =
        ExpressionParser.parseExpression(Tokenizer(expression), this).simplify().toString()

}