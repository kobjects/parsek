package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable
import org.kobjects.parserlib.examples.expressions.Settable

class CellReference(val col: Int, val row: Int) : Evaluable {

    constructor(name: String) : this(name[0].digitToInt(radix = 36) - 10, name.substring(1).toInt() - 1)

    override fun eval(ctx: Context): Any {
        return (ctx as Spreadsheet)[col, row].value ?: 0
    }

    override fun evalString(ctx: Context): String {
        return (ctx as Spreadsheet)[col, row].value?.toString() ?: ""
    }

    fun set(ctx: Context, expression: Evaluable) {
        (ctx as Spreadsheet)[col, row].expression = expression
    }

}