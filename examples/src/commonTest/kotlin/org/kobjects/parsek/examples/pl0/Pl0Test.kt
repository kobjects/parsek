package org.kobjects.parsek.examples.pl0

import org.kobjects.parsek.examples.pl0.parser.Pl0Parser
import kotlin.test.Test
import kotlin.test.assertEquals

class Pl0Test {
    val fizzBuzz = """
            VAR x;
            PROCEDURE fizzBuzz;
            BEGIN
                x := 1;
                WHILE x <= 20 DO
                BEGIN
                  IF x / 3 * 3 = x THEN
                  BEGIN
                    IF x / 5 * 5 = x THEN ! -15;
                    IF x / 5 * 5 # x THEN ! -3
                  END;
                  IF x / 3 * 3 # x THEN
                  BEGIN
                    IF x / 5 * 5 = x THEN ! -5;
                    IF x / 5 * 5 # x THEN ! x
                  END;
                  x := x + 1
                END
            END;
    
            CALL fizzBuzz.

    """.trimIndent()

    @Test
    fun testFizzBuzz() {
        val program = Pl0Parser.parseProgram(fizzBuzz)
        val result = mutableListOf<Int>()

        program.eval(
            { result.add(it) },
            { throw UnsupportedOperationException() })

        assertEquals(listOf(1, 2, -3, 4, -5, -3, 7, 8, -3, -5, 11, -3, 13, 14, -15, 16, 17, -3, 19, -5), result)
    }

    @Test
    fun testStringification() {
        val program = Pl0Parser.parseProgram(fizzBuzz)
        val reparsed1 = Pl0Parser.parseProgram(program.toString())
        assertEquals(program, reparsed1)

        // We may insert extra parens in expressions
        val reparsed2 = Pl0Parser.parseProgram(reparsed1.toString())
        assertEquals(reparsed1.toString(), reparsed2.toString())
    }
}