package org.kobjects.parsek.examples.expressions


class Symbol(
    val name: String,
    val children: List<Evaluable>,
    val precedence: Int = 0
) : Evaluable {
    constructor(name: String, precedence: Int, vararg children: Evaluable) : this(name, children.toList(), precedence)
    constructor(name: String, vararg children: Evaluable) : this(name, children.toList())
    override fun eval(context: RuntimeContext): Any = context.evalSymbol(name, children, context)

    override fun toString(): String = buildString { stringify(this, 0) }

    override fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        if (name.firstOrNull { !it.isLetterOrDigit() } != null) {
            if (parentPrecedence > 0 && parentPrecedence >= precedence) {
                stringBuilder.append("(")
                stringBuilder.append(this)
                stringBuilder.append(")")
            } else {
                when (children.size) {
                    0 -> throw IllegalStateException()
                    1 -> stringBuilder.append(name).append(children.first().toString())
                    else -> stringBuilder.append(children.joinToString(" $name "))
                }
            }
        } else {
            stringBuilder.append(name)
            if (children.isNotEmpty()) {
                stringBuilder.append(children.joinToString(", ", "(", ")"))
            }
        }

    }
}