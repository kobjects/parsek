package org.kobjects.parsek.examples.algebra

import kotlin.test.Test
import kotlin.test.assertEquals

class CasTest {

    @Test
    fun testSimpleExpressions() {
        val cas = Cas()
        assertEquals("-4.0", cas.process("4-4-4"))
        assertEquals("2.0 * x", cas.process("derive(x^2, x)"))
    }
}