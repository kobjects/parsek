package org.kobjects.parsek.examples.json

import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner

private val TRUE_REGEX = Regex.fromLiteral("true")
private val FALSE_REGEX = Regex.fromLiteral("false")
private val NULL_REGEX = Regex.fromLiteral("null")
private val COMMA_REGEX = Regex.fromLiteral(",")
private val ARRAY_START_REGEX = Regex.fromLiteral("[")
private val ARRAY_END_REGEX = Regex.fromLiteral("]")
private val OBJECT_START_REGEX = Regex.fromLiteral("{")
private val OBJECT_END_REGEX = Regex.fromLiteral("}")
private val COLON_REGEX = Regex.fromLiteral(":")

class JsonScanner(
    input: String
) : Scanner<JsonTokenType>(
    input,
    JsonTokenType.EOF,
    RegularExpressions.WHITESPACE to null,
    RegularExpressions.JSON_NUMBER to JsonTokenType.NUMBER,
    RegularExpressions.DOUBLE_QUOTED_STRING to JsonTokenType.STRING,
    TRUE_REGEX to JsonTokenType.TRUE,
    FALSE_REGEX to JsonTokenType.FALSE,
    NULL_REGEX to JsonTokenType.NULL,
    COMMA_REGEX to JsonTokenType.COMMA,
    ARRAY_START_REGEX to JsonTokenType.ARRAY_START,
    ARRAY_END_REGEX to JsonTokenType.ARRAY_END,
    OBJECT_START_REGEX to JsonTokenType.OBJECT_START,
    OBJECT_END_REGEX to JsonTokenType.OBJECT_END,
    COLON_REGEX to JsonTokenType.COLON,
)
