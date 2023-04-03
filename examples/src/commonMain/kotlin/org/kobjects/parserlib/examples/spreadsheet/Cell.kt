package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.Evaluable

class Cell(
    val spreadsheet: Spreadsheet,
) {
    var expression: Evaluable? = null
    var value: Any? = null


    fun update(): Boolean {
        var newValue: Any?
        try {
            newValue = expression?.eval(spreadsheet)
        } catch (e: Exception) {
            newValue = e
        }
        if (newValue == value) {
            return false
        }
        value = newValue
        return true
    }



}