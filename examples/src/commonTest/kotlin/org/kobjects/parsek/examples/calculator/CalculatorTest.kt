package org.kobjects.parsek.examples.calculator

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {

    @Test
    fun testSimpleExpressions() {
        assertEquals(-4.0, Calculator.calculate("4-4-4"))
    }
}