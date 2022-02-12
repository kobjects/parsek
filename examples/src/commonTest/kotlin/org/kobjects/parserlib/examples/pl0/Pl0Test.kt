package org.kobjects.parserlib.examples.pl0

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
        val program = parseProgram(Pl0Tokenizer(fizzBuzz)
        )

        val result = mutableListOf<Int>()

        println(program)

        program.eval(
            { result.add(it) },
            { throw UnsupportedOperationException() })

        assertEquals(listOf(1, 2, -3, 4, -5, -3, 7, 8, -3, -5, 11, -3, 13, 14, -15, 16, 17, -3, 19, -5), result)
    }
}