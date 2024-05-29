package org.kobjects.parsek.examples.basic

class Program {
    val lines = mutableListOf<Line>()

    suspend fun eval(interpreter: Interpreter) {
        while(interpreter.currentLineIndex < lines.size) {
            val lineIndex = interpreter.currentLineIndex
            lines[interpreter.currentLineIndex].eval(interpreter)
            // Goto inside the same line will be handled inside line.eval().
            if (lineIndex == interpreter.currentLineIndex) {
                interpreter.currentLineIndex++
            }
        }
    }


    fun indexOf(lineNumber: Int): Int {
        val index = lines.binarySearch { it.number.compareTo(lineNumber) }
        return if (index < 0) -index - 1 else index
    }


    fun setLine(lineNumber: Int, statements: List<Statement>?) {
        val index = lines.binarySearch { it.number.compareTo(lineNumber) }
        if (statements == null) {
            if (index > 0) {
                lines.removeAt(index)
            }
        } else {
            val newLine = Line(lineNumber, statements)
            if (index > 0) {
                lines.set(index, newLine)
            } else {
                lines.add(-index - 1, newLine)
            }
        }
    }

    fun find(
        kind: Statement.Kind,
        name: String?,
        position: IntArray
    ): Statement? {
        while (position[0] < lines.size) {
            val line = lines[position[0]]
            while (position[1] < line.statements.size) {
                val statement = line.statements[position[1]]
                if (statement.kind == kind) {
                    if (name == null || statement.params.size == 0) {
                        return statement
                    }
                    for (i in 0 until statement.params.size) {
                        if (statement.params.get(i).toString().equals(name, ignoreCase = true)) {
                            position[2] = i
                            return statement
                        }
                    }
                }
                position[1]++
            }
            position[0]++
            position[1] = 0
        }
        return null
    }


}