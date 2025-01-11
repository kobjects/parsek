package org.kobjects.parsek.expression

open class Operator private constructor(
    val precedence: Int,
    val names: Array<out String>
) {
    class Infix(precedence: Int, vararg name: String) : Operator(precedence, name)
    class Prefix(precedence: Int, vararg name: String) : Operator(precedence, name)
    class InfixRtl(precedence: Int, vararg name: String) : Operator(precedence, name)
    class Suffix(precedence: Int, vararg name: String) : Operator(precedence, name)
}