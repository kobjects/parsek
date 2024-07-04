package org.kobjects.parsek.examples.mython

import kotlin.test.Test
import kotlin.test.assertEquals

class MythonTest {

    val SQR = """
def main(x):
    x * x
"""

    @Test
    fun parsingTest() {
        val sqrProgram = MythonParser.parseProgram(SQR)

        assertEquals("def main(x):\n  mul(x, x)\n", sqrProgram.toString())

        assertEquals(4.0, sqrProgram.run(2.0))
    }
}