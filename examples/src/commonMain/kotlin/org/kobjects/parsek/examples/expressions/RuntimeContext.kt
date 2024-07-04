package org.kobjects.parsek.examples.expressions


interface RuntimeContext {
    fun evalSymbol(name: String, children: List<Evaluable>, parameterContext: RuntimeContext): Any

    fun numeric1(children: List<Evaluable>, f: (Double) -> Any): Any {
        require(children.size == 1)
        return f(children[0].evalDouble(this))
    }

    fun numeric2(children: List<Evaluable>, f: (Double, Double) -> Any) : Any {
        require(children.size == 2)
        return f(children[0].evalDouble(this), children[1].evalDouble(this))
    }
}