package org.kobjects.parsek.examples.calculator

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {

    @Test
    fun testSimpleExpressions() {
        assertEquals(-4.0, Calculator.calculate("4-4-4"))
        assertEquals(5.0, Calculator.calculate("3 + 2"))
        assertEquals(11.0, Calculator.calculate("3 + 2 * 4"))
    }
}