package org.kobjects.parserlib.examples.algebra

import org.kobjects.parserlib.examples.expressions.Builtin
import org.kobjects.parserlib.examples.expressions.Evaluable
import org.kobjects.parserlib.examples.expressions.Literal
import org.kobjects.parserlib.examples.expressions.Variable

fun Evaluable.derive(to: String): Evaluable {
    val self = this.simplify()
    return when(self) {
        is Builtin -> self.deriveImpl(to).simplify()
        is Variable -> if (self.name == to) Literal(1.0) else Literal(0.0)
        is Literal -> Literal(0.0)
        else -> throw IllegalArgumentException("Can't derive $this")
    }
}

fun Builtin.deriveImpl(to: String): Evaluable {
    return when(kind) {
        Builtin.Kind.ADD -> Builtin("+", param[0].derive(to), param[1].derive(to))
        Builtin.Kind.EXP -> Builtin("*", Builtin("EXP", param[0]),
                param[0].derive(to))
        Builtin.Kind.LOG -> Builtin("/", param[0].derive(to), param[0])
        Builtin.Kind.MUL -> Builtin("+",
            Builtin("*", param[0].derive(to), param[1]),
            Builtin("*", param[1].derive(to), param[0]))
        Builtin.Kind.POW ->
            if (param[1] is Literal) {
                val exp = ((param[1] as Literal).value as Number).toDouble()
                Builtin("*", param[0].derive(to), Builtin("*", Literal(exp), Builtin("^", param[0], Literal(exp - 1))))
            }
            else Builtin("EXP", Builtin("*", param[1], Builtin("LOG", param[0]))).simplify().derive(to)
        Builtin.Kind.SIN ->
            Builtin("*", Builtin("COS", param[0]), param[0].derive(to))

        else -> throw IllegalArgumentException("Can't derive $this")
    }
}