package org.kobjects.parsek.examples.expressions

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionsTest {

    @Test
    fun testSimpleExpressions() {

        assertEquals(-4.0, ExpressionParser.eval("4-4-4"))
        // assertEquals("Hello", ctx.eval("left(\"HelloWorld\", 5)"))
        assertEquals(4.0, ExpressionParser.eval("2**2"))
    }
}