package org.kobjects.parserlib.examples.pl0

abstract class Statement {
    abstract fun eval(context: EvaluationContext)
    abstract fun toString(indent: String): String
    override fun toString() = toString("")
}

data class Assignment(
    val variable: String,
    val expression: Expression
) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.set(variable, expression.eval(context))
    }
    override fun toString(indent: String) = "$variable := $expression"
}

data class Call(val name: String) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.call(name)
    }
    override fun toString(indent: String) = "CALL $name"
}

data class BeginEnd(
    val statements: List<Statement>
) : Statement() {
    override fun eval(context: EvaluationContext) {
        statements.forEach { it.eval(context) }
    }
    override fun toString(indent: String): String {
        val sb = StringBuilder("\n")
        sb.append(indent).append("BEGIN\n")
        for (i in statements.indices) {
            sb.append(indent).append("  ")
            sb.append(statements[i].toString("  " + indent))
            if (i < statements.size - 1) {
                sb.append(';')
            }
            sb.append('\n')
        }
        sb.append(indent).append("END")
        return sb.toString()
    }
}

data class Read(val variable: String) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.set(variable, context.globalContext.read())
    }
    override fun toString(indent: String) = "? $variable"
}

data class Write(val experession: Expression) : Statement() {
    override fun eval(context: EvaluationContext) {
        context.globalContext.write(experession.eval(context))
    }
    override fun toString(indent: String) = "! $experession"
}

data class If(val condition: Condition, val statement: Statement) : Statement() {
    override fun eval(context: EvaluationContext) {
        if (condition.eval(context)) {
            statement.eval(context)
        }
    }
    override fun toString(indent: String) = "IF $condition THEN ${statement.toString(indent)}"
}

data class While(val condition: Condition, val statement: Statement) : Statement() {
    override fun eval(context: EvaluationContext) {
        while (condition.eval(context)) {
            statement.eval(context)
        }
    }
    override fun toString(indent: String) = "WHILE $condition DO ${statement.toString(indent)}"
}

class EmptyStatement() : Statement() {
    override fun eval(context: EvaluationContext) {
    }

    override fun toString(indent: String) = ""
}