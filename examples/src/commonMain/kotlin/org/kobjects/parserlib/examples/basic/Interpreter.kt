package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.Context

class Interpreter : Context() {
    val arrayVariables = mutableListOf<MutableMap<String,MutableMap<Int,Any>>>()

}