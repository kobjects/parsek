package org.kobjects.parsek.examples.json

import kotlin.test.Test
import kotlin.test.assertEquals

class JsonTest {

    @Test
    fun testSimpleJson() {
        assertEquals(42.0, parseJson("42.0"))
        assertEquals(listOf(42.0, false) , parseJson("[42.0, false]"))
        assertEquals(mapOf("foo" to 42.0), parseJson("{\"foo\": 42.0}"))
    }
}