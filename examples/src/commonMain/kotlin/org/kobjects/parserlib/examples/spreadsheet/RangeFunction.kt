package org.kobjects.parserlib.examples.spreadsheet

import org.kobjects.parserlib.examples.expressions.Context
import org.kobjects.parserlib.examples.expressions.Evaluable

class RangeFunction(val kind: Kind, vararg val params: Evaluable) : Evaluable {

    enum class Kind(val op: (List<Double>) -> Double) {
        SUM( { it.sum() } )
    }

    override fun eval(ctx: Context): Any {
        val list = mutableListOf<Double>()
        for (param in params) {
            val v = param.eval(ctx)
            if (v is Number) {
                list.add(v.toDouble())
            } else if (v is List<*>) {
                for (element in v) {
                    list.add((v as Number).toDouble())
                }
            }
        }
        return kind.op(list)
    }
}