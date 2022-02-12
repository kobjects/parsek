package org.kobjects.parserlib.pl0


interface Statement {
    fun eval(context: EvaluationContext)
}

data class Assignment(
    val variable: String,
    val expression: Expression
) : Statement {
    override fun eval(context: EvaluationContext) {
        context.set(variable, expression.eval(context))
    }
}

data class Call(val name: String) : Statement {
    override fun eval(context: EvaluationContext) {
        context.call(name)
    }
}

data class BeginEnd(
    val statements: List<Statement>
) : Statement {
    override fun eval(context: EvaluationContext) {
        statements.forEach { it.eval(context) }
    }
}

data class Read(val variable: String) : Statement {
    override fun eval(context: EvaluationContext) {
        context.set(variable, context.globalContext.read())
    }
}

data class Write(val experession: Expression) : Statement {
    override fun eval(context: EvaluationContext) {
        context.globalContext.write(experession.eval(context))
    }
}

data class If(val condition: Condition, val statement: Statement) : Statement {
    override fun eval(context: EvaluationContext) {
        if (condition.eval(context)) {
            statement.eval(context)
        }
    }
}

data class While(val condition: Condition, val statement: Statement) : Statement {
    override fun eval(context: EvaluationContext) {
        while (condition.eval(context)) {
            statement.eval(context)
        }
    }
}