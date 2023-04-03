package org.kobjects.parserlib.examples.algebra

import org.kobjects.parserlib.examples.expressions.Builtin
import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable
import org.kobjects.parserlib.examples.expressions.Literal

fun Evaluable.simplify(): Evaluable = when(this) {
    is Builtin -> this.simplify()
    else -> this
}

fun Builtin.simplify(): Evaluable {
    val param = List(param.size) { param[it].simplify() }.toTypedArray()
    return when(kind) {
        Builtin.Kind.ADD ->
            if (param[0] is Literal && param[1] is Literal) Literal(eval(Context()))
            else if (param[1] is Literal) Builtin("+", param[1], param[0]).simplify()
            else if (param[0].is0()) param[1]
            else if (param[1].is0()) param[0]
            else if (param[0] is Literal
                && (param[1].isBuiltin(Builtin.Kind.ADD) || param[1].isBuiltin(Builtin.Kind.SUB))
                && (param[1] as Builtin).param[0] is Literal)
                Builtin(
                    (param[1] as Builtin).kind,
                    Literal(param[0].evalDouble(Context()) + (param[1] as Builtin).param[0].evalDouble(Context())),
                    (param[1] as Builtin).param[1])
            else Builtin(kind, *param)
        Builtin.Kind.SUB ->
            if (param[0] is Literal && param[1] is Literal) Literal(eval(Context()))
            else if (param[0].is0()) Builtin(Builtin.Kind.NEG, param[1])
            else if (param[1].is0()) param[0]
            else if (param[1] is Literal) Builtin("+", Literal(-param[1].evalDouble(Context())), param[0])
            else Builtin(kind, *param)
        Builtin.Kind.MUL ->
            if (param[0] is Literal && param[1] is Literal) Literal(eval(Context()))
            else if (param[1] is Literal) Builtin("*", param[1], param[0]).simplify()
            else if (param[0].is0()) Literal(0.0)
            else if (param[0].is1()) param[1]
            else if (param[0] is Literal
                && (param[1].isBuiltin(Builtin.Kind.MUL) || param[1].isBuiltin(Builtin.Kind.DIV))
                && (param[1] as Builtin).param[0] is Literal)
                Builtin(
                    (param[1] as Builtin).kind,
                    Literal(param[0].evalDouble(Context()) * (param[1] as Builtin).param[0].evalDouble(Context())),
                    (param[1] as Builtin).param[1])
            else Builtin(kind, *param)
        Builtin.Kind.POW ->
            if (param[1].is1()) param[0]
            else if (param[1].is0()) Literal(1.0)
            else if (param[0].is1()) Literal(1.0)
            else Builtin(kind, *param)
        else -> Builtin(kind, *param)
    }
}