package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable
import org.kobjects.parserlib.examples.expressions.Variable

class RangeReference(from: Evaluable, to: Evaluable) : Evaluable, Settable {
    val from = from as CellReference
    val to = to as CellReference
    override fun eval(ctx: Context): Any {
        val result = mutableListOf<Any>()
        for (rowIndex in from.row .. to.row) {
            for (colIndex in from.col .. to.col ) {
                result.add((ctx as Spreadsheet)[colIndex, rowIndex].value ?: 0.0)
            }
        }
        return result
    }

    override fun set(spreadsheet: Spreadsheet, expression: Evaluable?) {
        for (rowIndex in from.row .. to.row) {
            for (colIndex in from.col .. to.col ) {
                spreadsheet[colIndex, rowIndex].expression = expression
            }
        }
    }
}