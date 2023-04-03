package org.kobjects.parserlib.examples.expressions

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionsTest {

    @Test
    fun testSimpleExpressions() {
        assertEquals(-4.0, ExpressionParser.eval("4-4-4"))
        assertEquals("Hello", ExpressionParser.eval("left(\"HelloWorld\", 5)"))
        assertEquals(4.0, ExpressionParser.eval("2^2"))
    }
}