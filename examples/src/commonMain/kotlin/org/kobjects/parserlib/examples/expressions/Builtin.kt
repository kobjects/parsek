package org.kobjects.parserlib.examples.expressions

import kotlin.math.*
import kotlin.random.Random

class Builtin(val kind: Kind, vararg val param: Evaluable) : Evaluable {

    constructor(name: String, vararg param: Evaluable) : this(
        Kind.values().first() { it != Kind.NEG && it.toString() == name }, *param)

    enum class Kind(
        val symbol: String? = null,
        val precedence: Int = 0,
        val parameterCount: Int = 1,
        val minParameterCount: Int = parameterCount,
    ) {
        ADD("+", precedence = 4, parameterCount = 2),
        AND(precedence = 2, parameterCount = 2),
        ABS,
        ASC,
        ATN,
        CHR,
        CHR_("CHR$"),
        COS,
        DIV("/", precedence = 5, parameterCount = 2),
        EMPTY(""),  // Represents grouping brackts
        EQ("=", precedence = 3, parameterCount = 2),
        EXP,
        GE(">=", precedence = 3, parameterCount = 2),
        GT(">", precedence = 3, parameterCount = 2),
        INT,
        LE("<=", precedence = 3, parameterCount = 2),
        LEFT(parameterCount = 2),
        LEFT_("LEFT$", parameterCount = 2),
        LT("<", precedence = 3, parameterCount = 2),
        LEN,
        LOG,
        MID(parameterCount = 3, minParameterCount = 2),
        MID_("MID$", parameterCount = 3, minParameterCount = 2),
        MUL("*", precedence = 5, parameterCount = 2),
        NE("<>", precedence = 3, parameterCount = 2),
        NEG("-", precedence = 6),
        NOT(precedence = 0),
        OR(precedence = 1, minParameterCount = 2),
        POW("^", precedence = 7, minParameterCount = 2),
        RIGHT(parameterCount = 2, minParameterCount = 1),
        RIGHT_("RIGHT$", parameterCount = 2, minParameterCount = 1),
        RND(minParameterCount = 0),
        SIN,
        SGN,
        SUB("-", precedence = 4, parameterCount = 2),
        SQR,
        STR,
        TAB,
        TAN,
        VAL;

        override fun toString(): String = symbol ?: name
    }

    override fun precedence() = kind.precedence

    private fun compare(context: Context, predicate: (Int) -> Boolean) =
        if (predicate((param[0].eval(context) as Comparable<Any>).compareTo(
                param[1].eval(context) as Comparable<Any>))) 1 else 0


    override fun eval(ctx: Context) = when(kind) {
        Kind.ABS -> abs(param[0].evalDouble(ctx))
        Kind.AND -> param[0].evalInt(ctx) and param[1].evalInt(ctx)
        Kind.ADD -> {
            val left = param[0].eval(ctx)
            if (left is Number) left.toDouble() + param[1].evalDouble(ctx)
            else left as String + param[1].evalString(ctx)
        }
        Kind.ASC -> param[0].evalString(ctx)[0].code.toDouble()
        Kind.ATN -> atan(param[0].evalDouble(ctx))
        Kind.CHR_,
        Kind.CHR -> Char(param[0].evalInt(ctx)).toString()
        Kind.COS -> cos(param[0].evalDouble(ctx))
        Kind.DIV -> param[0].evalDouble(ctx) / param[1].evalDouble(ctx)
        Kind.EMPTY -> param[0].evalDouble(ctx)
        Kind.EQ -> param[0].eval(ctx) == param[1].eval(ctx)
        Kind.EXP -> exp(param[0].evalDouble(ctx))
        Kind.GE -> compare(ctx) { it >= 0}
        Kind.GT -> compare(ctx) { it > 0}
        Kind.INT -> floor(param[0].evalDouble(ctx))
        Kind.LE -> compare(ctx) { it <= 0}
        Kind.LT -> compare(ctx) { it < 0}
        Kind.LEN -> param[0].evalString(ctx).length
        Kind.LEFT_,
        Kind.LEFT -> param[0].evalString(ctx).substring(0, param[1].evalInt(ctx))
        Kind.LOG -> ln(param[0].evalDouble(ctx))
        Kind.MID_,
        Kind.MID -> {
            val s = param[0].evalString(ctx)
            val start = param[1].evalInt(ctx)
            if (param.size > 2) s.substring(start, param[2].evalInt(ctx))
            else s.substring(start)
        }
        Kind.MUL -> param[0].evalDouble(ctx) * param[1].evalDouble(ctx)
        Kind.NE -> param[0].eval(ctx) != param[1].eval(ctx)
        Kind.NEG -> -param[0].evalDouble(ctx)
        Kind.NOT -> param[0].evalInt(ctx).inv()
        Kind.OR -> param[0].evalInt(ctx) or param[1].evalInt(ctx)
        Kind.POW -> param[0].evalDouble(ctx).pow(param[1].evalDouble(ctx))
        Kind.RIGHT_,
        Kind.RIGHT -> {
            val s = param[0].evalString(ctx)
            val count = if (param.size > 1)  param[1].evalInt(ctx) else 1
            s.substring(max(0, s.length - count))
        }
        Kind.RND -> if (param.size == 1) Random(param[0].evalInt(ctx)).nextDouble() else Random.nextDouble()
        Kind.SIN -> sin(param[0].evalDouble(ctx))
        Kind.SGN -> {
            val v = param[0].evalDouble(ctx)
            if (v > 0.0) 1.0 else if (v < 0.0) -1.0 else 0.0
        }
        Kind.SUB -> param[0].evalDouble(ctx) - param[1].evalDouble(ctx)
        Kind.STR -> param[0].evalDouble(ctx).toString()
        Kind.SQR -> sqrt(param[0].evalDouble(ctx))
        Kind.TAN -> tan(param[0].evalDouble(ctx))
        Kind.VAL -> param[0].evalString(ctx).toDouble()
        Kind.TAB -> {
            val sb = StringBuilder()
            for (i in 0 until param[0].evalInt(ctx)) {
                sb.append("   ")
            }
            sb.toString()
        }
    }

    override fun toString(): String {
        if (precedence() != 0) {
            return if (param.size == 1)
                "$kind ${param[0].parenthesize(precedence())}"
            else
                param[0].parenthesize(precedence()) +
                        (if (precedence() >= Kind.MUL.precedence) "$kind" else " $kind ") +
                        param[1].parenthesize(precedence() - 1)

        }
        return "$kind(${param.joinToString { it.toString() }})"
    }

    init {
        require(param.size == kind.parameterCount
                || param.size >= kind.minParameterCount) { "Parameter count mismatch." }
    }
}