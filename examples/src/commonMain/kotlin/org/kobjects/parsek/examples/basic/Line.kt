package org.kobjects.parsek.examples.basic

class Line(
    val number: Int,
    val statements: List<Statement>
) {
    override fun toString() = number.toString() + " " + statements.joinToString(" : ")

    suspend fun eval(interpreter: Interpreter) {
        val currentLine = interpreter.currentLineIndex
        while (interpreter.currentStatementIndex < statements.size) {
            val index = interpreter.currentStatementIndex
            statements[index].eval(interpreter)
            if (interpreter.currentLineIndex != currentLine) {
                return  // Goto or similar out of the current line
            }
            if (interpreter.currentStatementIndex == index) {
                interpreter.currentStatementIndex++
            }
        }
        interpreter.currentStatementIndex = 0
    }

}