package org.kobjects.parsek.examples.basic

import org.kobjects.parsek.examples.expressions.Context
import org.kobjects.parsek.examples.expressions.Evaluable

object InvisibleStringLiteral : Evaluable {
    val INVISIBLE_STRING = ""

    override fun eval(ctx: Context) = INVISIBLE_STRING

    override fun toString() = INVISIBLE_STRING
}