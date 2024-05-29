package org.kobjects.parsek.examples.basic

import org.kobjects.parsek.examples.expressions.Variable

class StackEntry(
    val lineIndex: Int,
    val statementIndex: Int,
    val forVariable: Variable? = null,
    val step: Double = 1.0,
    val end: Double = 0.0
)