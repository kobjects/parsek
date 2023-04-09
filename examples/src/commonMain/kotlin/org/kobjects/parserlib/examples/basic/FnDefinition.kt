package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.Evaluable

class FnDefinition(val parameterNames: List<String>, val expression: Evaluable) {

    fun eval(interpreter: Interpreter, params: List<Evaluable>): Any {
        val saved = arrayOfNulls<Any>(parameterNames.size)
        for (i in parameterNames.indices) {
            val param = parameterNames[i]
            saved[i] = interpreter.variables[param]
            interpreter.variables[param] = params[i].eval(interpreter)
        }
        return try {
            expression.eval(interpreter)
        } finally {
            for (i in parameterNames.indices) {
                val restore = saved[i]
                val name = parameterNames[i]
                if (restore == null) {
                    interpreter.variables.remove(name)
                } else {
                    interpreter.variables[name] = restore
                }
            }
        }
    }
}