package org.kobjects.parserlib.examples.calculator

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {

    @Test
    fun testSimpleExpressions() {
        assertEquals(-4.0, Calculator.eval("4-4-4"))
    }
}