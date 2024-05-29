package org.kobjects.parsek.examples.expressions

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionsTest {

    @Test
    fun testSimpleExpressions() {
        val ctx = Context()
        assertEquals(-4.0, ctx.eval("4-4-4"))
        assertEquals("Hello", ctx.eval("left(\"HelloWorld\", 5)"))
        assertEquals(4.0, ctx.eval("2^2"))
        assertEquals(0.0, ctx.eval("x"))
        assertEquals(Unit, ctx.eval("x = 4"))
        assertEquals(4.0, ctx.eval("x"))
    }
}