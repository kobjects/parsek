package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable

class CellReference(val col: Int, val row: Int) : Evaluable, Settable {

    constructor(name: String) : this(name[0].digitToInt(radix = 36) - 10, name.substring(1).toInt() - 1)

    override fun eval(ctx: Context): Any {
        return (ctx as Spreadsheet)[col, row].value ?: 0
    }

    override fun evalString(ctx: Context): String {
        return (ctx as Spreadsheet)[col, row].value?.toString() ?: ""
    }

    override fun set(spreadsheet: Spreadsheet, expression: Evaluable?) {
        spreadsheet[col, row].expression = expression
    }

}