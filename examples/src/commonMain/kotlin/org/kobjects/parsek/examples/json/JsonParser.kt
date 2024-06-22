package org.kobjects.parsek.examples.json

fun parseJson(jsonString: String): Any? = parseJson(JsonScanner(jsonString))

fun parseJson(jsonScanner: JsonScanner): Any? {
    val result = parseValue(jsonScanner)
    if (!jsonScanner.eof) {
        throw jsonScanner.exception("End of input expected.")
    }
    return result
}

fun unquote(s: String): String {
    // Need to unescape here, too
    return s.substring(1, s.length - 1)
}

fun parseValue(jsonScanner: JsonScanner): Any? {
    val token = jsonScanner.consume()
    return when (token.type) {
        JsonTokenType.NUMBER -> token.text.trim().toDouble()
        JsonTokenType.STRING -> unquote(token.text)
        JsonTokenType.TRUE -> true
        JsonTokenType.FALSE -> false
        JsonTokenType.NULL -> null
        JsonTokenType.ARRAY_START -> parseArray(jsonScanner)
        JsonTokenType.OBJECT_START -> parseObject(jsonScanner)
        JsonTokenType.ARRAY_END,
        JsonTokenType.OBJECT_END,
        JsonTokenType.COLON,
        JsonTokenType.COMMA -> throw jsonScanner.exception("Unexpected token while parsing a value.")
        JsonTokenType.EOF -> throw jsonScanner.exception("Unexpected end of input while parsing a value.")
    }
}

fun parseArray(jsonScanner: JsonScanner): List<Any?> {
    val result = mutableListOf<Any?>()
    if (jsonScanner.current.text != "]") {
        while (true) {
            result.add(parseValue(jsonScanner))
            if (!jsonScanner.tryConsume(",")) {
                break
            }
        }
    }
    jsonScanner.consume("]")
    return result.toList()
}

fun parseObject(jsonScanner: JsonScanner): Map<String, Any?> {
    val result = mutableMapOf<String, Any?>()
    if (jsonScanner.current.type != JsonTokenType.OBJECT_END) {
        while (true) {
            val key = unquote(jsonScanner.consume(JsonTokenType.STRING).text)
            jsonScanner.consume(JsonTokenType.COLON)
            val value = parseValue(jsonScanner)
            result[key] = value
            if (!jsonScanner.tryConsume(",")) {
                break
            }
        }
    }
    jsonScanner.consume(JsonTokenType.OBJECT_END)
    return result.toMap()
}