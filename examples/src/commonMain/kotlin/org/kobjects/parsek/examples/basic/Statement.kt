package org.kobjects.parsek.examples.basic

import org.kobjects.parsek.examples.expressions.Evaluable
import org.kobjects.parsek.examples.expressions.Settable

class Statement(
    val kind: Kind,
    vararg val params: Evaluable,
    val delimiters: List<String> = emptyList(),
) {

    enum class Kind {
        CLEAR, CONTINUE,
        DATA, DEF, DIM, DUMP,
        EMPTY, END,
        FOR,
        GOTO, GOSUB,
        IF, INPUT,
        LET, LIST, LOAD,
        NEW, NEXT,
        ON,
        PRINT,
        READ,
        REM, RESTORE, RUN, RETURN,
        SAVE, STOP,
        TRON, TROFF
    }

    suspend fun eval(interpreter: Interpreter) {
        if (interpreter.trace && interpreter.currentLineIndex != -1) {
            val line = interpreter.program.lines[interpreter.currentLineIndex]
            interpreter.printFn("$line : ${interpreter.currentStatementIndex} : $this")
        }
        when (kind) {
            Kind.CLEAR -> interpreter.clear()
            Kind.CONTINUE -> interpreter.continueCommand()
            Kind.DATA -> {}
            Kind.DEF -> interpreter.defFn(params[0])
            Kind.DIM -> {}
            Kind.DUMP -> interpreter.dump()
            Kind.EMPTY -> {}
            Kind.END -> interpreter.goto(Int.MAX_VALUE)
            Kind.FOR -> interpreter.forStatement(params)
            Kind.GOTO -> interpreter.goto(params[0].evalInt(interpreter))
            Kind.GOSUB -> interpreter.gosub(params[0].evalInt(interpreter))
            Kind.IF -> interpreter.ifStatement(params[0].evalBoolean(interpreter), if (params.size == 2) params[1] else null)
            Kind.INPUT -> interpreter.input(params, delimiters)
            Kind.LET -> (params[0] as Settable).set(interpreter, params[1])
            Kind.LIST -> interpreter.listCommand()
            Kind.LOAD -> interpreter.load(params[0].evalString(interpreter))
            Kind.NEW -> interpreter.new()
            Kind.NEXT -> interpreter.next(params)
            Kind.ON -> interpreter.on(params, delimiters[0] == " GOSUB ")
            Kind.PRINT -> interpreter.print(params, delimiters)
            Kind.READ -> interpreter.read(params)
            Kind.REM -> {}
            Kind.RESTORE -> interpreter.restore(if (params.isEmpty()) null else params[0].evalInt(interpreter))
            Kind.RETURN -> interpreter.returnStatement()
            Kind.RUN -> interpreter.runCommand()
            Kind.SAVE -> interpreter.save(params[0].evalString(interpreter))
            Kind.STOP -> interpreter.stop()
            Kind.TROFF -> interpreter.trace = false
            Kind.TRON -> interpreter.trace = true
        }
    }

    override fun toString(): String {
        val sb = StringBuilder(" ")
        sb.append(kind.name)
        if (params.isNotEmpty()) {
            sb.append(' ')
            sb.append(params[0])
            for (i in 1 until params.size) {
                sb.append(if (i > delimiters.size) ", " else delimiters[i - 1])
                sb.append(params[i])
            }
            if (delimiters.size == params.size) {
                sb.append(delimiters[delimiters.size - 1])
            }
        }
        return sb.toString()
    }
}