package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable

object InvisibleStringLiteral : Evaluable {
    val INVISIBLE_STRING = ""

    override fun eval(ctx: Context) = INVISIBLE_STRING

}