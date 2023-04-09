package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.*
import kotlin.math.max

class Spreadsheet : Context() {
    val rows = mutableListOf<MutableList<Cell>>(mutableListOf())
    var unstable = false

    operator fun get(colIndex: Int, rowIndex: Int): Cell {
        while (rows.size <= rowIndex) {
            rows.add(mutableListOf())
        }
        val row = rows[rowIndex]
        while (row.size <= colIndex) {
            row.add(Cell(this))
        }
        return row[colIndex]
    }

    override fun resolveVariable(name: String) = CellReference(name)

    override fun resolveFunction(name: String, parameters: List<Evaluable>): Evaluable {
        val upper = name.uppercase()
        for (kind in RangeFunction.Kind.values()) {
            if (kind.name == upper) {
                return RangeFunction(kind, *parameters.toTypedArray())
            }
        }
        return super.resolveFunction(name, parameters)
    }

    fun update() {
        var changed = false
        for (i in 0..100) {
            changed = false
            for (row in rows) {
                for (cell in row) {
                    changed = cell.update() || changed
                }
            }
            if (!changed) {
                break
            }
        }
        unstable = changed
    }

    fun list(): List<String> {
        update()
        val result = mutableListOf<String>()
        var maxColumn = 0
        for (rowIndex in rows.indices) {
            val sb = StringBuilder()
            sb.append((rowIndex + 1) / 10)
            sb.append((rowIndex + 1) % 10)
            sb.append(' ')
            val row = rows[rowIndex]
            for (colIndex in row.indices) {
                val cell = row[colIndex]
                val content = cell.value?.toString() ?: ""
                if (cell.value !is Number) {
                    sb.append(content)
                }
                for (i in 0 until 11 - content.length) {
                    sb.append(' ')
                }
                if (cell.value is Number) {
                    sb.append(content)
                }
                maxColumn = max(maxColumn, colIndex)
            }
            result.add(sb.toString())
        }
        val sb = StringBuilder("   ")
        for (i in 0 ..maxColumn) {
            sb.append("     ")
            sb.append((i + 10).toString(36).uppercase())
            sb.append("     ")
        }
        result.add(0, sb.toString())
        return result
    }

    fun process(input: String): List<String> {
        val tokenizer = Tokenizer(input)
        if (tokenizer.eof || tokenizer.tryConsume("list")) {
            return list()
        } else if (tokenizer.current.type == TokenType.IDENTIFIER && tokenizer.lookAhead(1).text == "=" || tokenizer.tryConsume("let")) {
            val expr = ExpressionParser.parseExpression(tokenizer, this)
            require (expr is Builtin && expr.kind == Builtin.Kind.EQ) { "Assignment expression expected" }
            require(expr.param[0] is CellReference) { "Assignment target must be a cell reference." }
            (expr.param[0] as CellReference).set(this, expr.param[1])
            return emptyList()
        }
        throw tokenizer.exception("Unrecognized command: '${tokenizer.current.text}'")
    }


}