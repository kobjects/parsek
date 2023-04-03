package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.Evaluable

interface Settable {

    fun set(spreadsheet: Spreadsheet, expression: Evaluable?)

}