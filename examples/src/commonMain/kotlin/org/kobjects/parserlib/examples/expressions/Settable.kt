package org.kobjects.parserlib.examples.expressions

import org.kobjects.parserlib.examples.spreadsheet.Spreadsheet

interface Settable {

    fun set(ctx: Context, value: Any)

}