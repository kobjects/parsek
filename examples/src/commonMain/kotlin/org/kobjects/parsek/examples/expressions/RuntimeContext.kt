package org.kobjects.parsek.examples.expressions


interface RuntimeContext {
    fun evalSymbol(name: String, children: List<Node>): Any

    fun eval(expression: String) = ExpressionParser.parseExpression(Tokenizer(expression)).eval(this)

    fun numeric1(children: List<Node>, f: (Double) -> Any): Any {
        require(children.size == 1)
        return f(children[0].evalDouble(this))
    }

    fun numeric2(children: List<Node>, f: (Double, Double) -> Any) : Any {
        require(children.size == 2)
        return f(children[0].evalDouble(this), children[1].evalDouble(this))
    }
}