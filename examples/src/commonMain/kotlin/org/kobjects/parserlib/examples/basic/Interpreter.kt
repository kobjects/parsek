package org.kobjects.parserlib.examples.basic

import org.kobjects.parserlib.examples.expressions.*
import kotlin.math.max

class Interpreter(
    val printFn: (String) -> Unit = { println(it) },
    val readFn: (String) -> String = {
        if (it.isNotEmpty()) {
            printFn(it)
        }
        readln()
         },
    val loadFn: (name: String) -> String = { throw UnsupportedOperationException() },
    val saveFn: (name: String, content: String) -> Unit = { _, _ -> throw UnsupportedOperationException() },
) : Context() {
    val program = Program()
    val arrayVariables = mutableListOf<MutableMap<String,MutableMap<Int,Any>>>()
    val stack = mutableListOf<StackEntry>()

    var trace = false
    var pendingOutput = ""
    var currentStatementIndex = 0
    var currentLineIndex = 0
    var nextSubIndex = 0

    var currentTabPos = 0
    var stoppedAt: Pair<Int, Int>? = null
    var dataPosition = IntArray(3)
    var dataStatement: Statement? = null

    fun clear() {
        arrayVariables.clear()
        variables.clear()
    }

    fun continueCommand() {
        val stoppedAt = stoppedAt ?:
            throw IllegalStateException("Not stopped.")

        currentLineIndex = stoppedAt.first
        currentStatementIndex = stoppedAt.second
    }

    fun def(params: Array<out Evaluable>) {

    }
    fun dump() {
        TODO("Not yet implemented")
    }

    fun forStatement(params: Array<out Evaluable>) {
        val loopVar = params[0] as Variable
        loopVar.set(this, params[1])
        val current = loopVar.evalDouble(this)
        val end = params[2].evalDouble(this)
        val step = if (params.size > 3) params[3].evalDouble(this) else 1.0
        if (signum(step) == signum(current.compareTo(end))) {
            val nextPosition = IntArray(3)
            if (program.find(
                    Statement.Kind.NEXT,
                    params[0].toString(),
                    nextPosition) == null) {
                throw RuntimeException("FOR without NEXT")
            }
            currentLineIndex = nextPosition[0]
            currentStatementIndex = nextPosition[1]
            nextSubIndex = nextPosition[2] + 1
        } else {
            val entry: StackEntry = StackEntry(
                currentLineIndex,
                currentStatementIndex,
                loopVar,
                step = step,
                end = end
            )
            stack.add(entry)
        }
    }

    fun gosub(lineNumber: Int) {
        stack.add(StackEntry(currentLineIndex, currentStatementIndex))
        goto(lineNumber)
    }

    fun goto(lineNumber: Int) {
        currentLineIndex = program.indexOf(lineNumber)
        currentStatementIndex = 0
    }

    fun ifStatement(condition: Boolean, elseGoto: Evaluable?) {
        if (!condition) {
            currentStatementIndex = Int.MAX_VALUE
        } else if (elseGoto != null) {
            goto(elseGoto.evalInt(this))
        }
    }

    fun input(params: Array<out Evaluable>, delimiters: List<String>) {
        if (pendingOutput.isNotEmpty()) {
            print("\n")
        }
        val label = StringBuilder()
        for (i in params.indices) {
            val child = params[i]
            if (child is Settable) {
                if (i <= 0 || i > delimiters.size || delimiters.get(i - 1) != ", ") {
                    label.append("? ")
                }
                val variable = child as Settable
                var value: Any
                while (true) {
                    value = readFn(label.toString())
                    if (variable.toString().endsWith("$")) {
                        break
                    }
                    try {
                        value = value.toDouble()
                        break
                    } catch (e: NumberFormatException) {
                        print("Not a number. Please enter a number: ")
                    }
                }
                label.clear()
                variable.set(this, value)
            } else {
                label.append(child.eval(this))
            }
        }
    }

    fun listCommand() {
        for (line in program.lines) {
            printFn(line.toString())
        }
    }

    fun load(fileName: String) {
        val code = loadFn(fileName)
        val lines = code.split("\n")
        new()
        for (line in lines) {
            processInputLine(line)
        }
    }

    fun new() {
        clear()
        program.lines.clear()
    }

    fun next(params: Array<out Evaluable>) {
        for (i in nextSubIndex until max(params.size, 1)) {
            val name: String? = if (params.isEmpty()) null else params[i].toString()
            var entry: StackEntry
            while (true) {
                if (stack.isEmpty()
                    || stack.get(stack.size - 1).forVariable == null
                ) {
                    throw IllegalStateException("NEXT $name without FOR.")
                }
                entry = stack.removeAt(stack.size - 1)
                if (name == null || entry.forVariable?.name == name) {
                    break
                }
            }
            val loopVariable = entry.forVariable!!
            val current = loopVariable.evalDouble(this) + entry.step
            loopVariable.set(this, Literal(current))
            if (signum(entry.step) !== signum(current.compareTo(entry.end))) {
                stack.add(entry)
                currentLineIndex = entry.lineIndex
                currentStatementIndex = entry.statementIndex + 1
                break
            }
        }
        nextSubIndex = 0
    }


    fun on(params: Array<out Evaluable>, gosub: Boolean) {
        val index = params[0].evalInt(this)
        if (index < params.size && index > 0) {
            val line = params[index].evalInt(this)
            if (gosub) {
                gosub(line)
            } else {
                goto(line)
            }
        }
    }

    fun print(s: String) {
        pendingOutput += s
        var cut = 0
        while (true) {
            val newLine = pendingOutput.indexOf("\n", cut)
            if (newLine == -1) {
                break
            }
            printFn(pendingOutput.substring(cut, newLine))
            cut = newLine + 1
        }
        pendingOutput = pendingOutput.substring(cut)
    }

    fun print(params: Array<out Evaluable>, delimiters: List<String>) {
        for (i in 0 until params.size) {
            val value = params[i].eval(this)
            if (value is Double) {
                print((if (value < 0) "" else " ") + value + " ")
            } else {
                print(value)
            }
            if (i < delimiters.size && delimiters[i] == ", ") {
                print("                    ".substring(0, 14 - currentTabPos % 14))
            }
        }
        if (delimiters.size < params.size &&
            (params.isEmpty() || !params[params.size - 1].toString().startsWith("TAB"))) {
            print("\n")
        }
    }

    fun processInputLine(line: String): Boolean {
        val tokenizer = Tokenizer(line)
        return when (tokenizer.current.type) {
            TokenType.EOF -> false
            TokenType.NUMBER -> {
                val lineNumber = tokenizer.consume().text.toInt()
                if (tokenizer.current.type === TokenType.EOF) {
                    program.setLine(lineNumber, null)
                } else {
                    program.setLine(lineNumber, Parser.parseStatementList(tokenizer, this))
                }
                false
            }
            else -> {
                val line = Line(-2, Parser.parseStatementList(tokenizer, this))
                currentStatementIndex = 0
                currentLineIndex = -1
                line.eval(this)
                if (currentLineIndex != -1) {
                    program.eval(this)
                }
                true
            }
        }
    }


    fun read(params: Array<out Evaluable>) {
        for (child in params) {
            while (dataStatement == null
                || dataPosition.get(2) >= dataStatement!!.params.size
            ) {
                dataPosition[2] = 0
                if (dataStatement != null) {
                    dataPosition[1]++
                }
                dataStatement = program.find(
                    Statement.Kind.DATA,
                    null,
                    dataPosition
                )
                if (dataStatement == null) {
                    throw IllegalStateException("Out of data.")
                }
            }
            (child as Settable).set(this, dataStatement!!.params[dataPosition[2]++].eval(this))
        }
    }

    fun restore(lineNumber: Int?) {
        dataStatement = null
        dataPosition.fill(0)
        if (lineNumber != null) {
            dataPosition[0] = program.indexOf(lineNumber)
        }
    }

    fun returnStatement() {
        while (!stack.isEmpty()) {
            val entry = stack.removeLast()
            if (entry.forVariable == null) {
                currentLineIndex = entry.lineIndex
                currentStatementIndex = entry.statementIndex + 1
                return
            }
        }
        throw IllegalStateException("RETURN without GOSUB.")
    }

    fun runCommand() {
        clear()
        currentLineIndex = 0
        currentStatementIndex = 0
        program.eval(this)
    }

    fun stop() {
        stoppedAt = currentLineIndex to currentStatementIndex
        goto(Int.MAX_VALUE)
    }

    fun save(fileName: String) {
        saveFn(fileName, program.toString())
    }


    companion object {

        fun signum(value: Double): Int = if (value < 0.0) -1 else if (value > 0.0) 1 else 0
        fun signum(value: Int): Int = if (value < 0) -1 else if (value > 0) 1 else 0

    }
}