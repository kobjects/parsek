package org.kobjects.parsek.examples.json

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner

class JsonScanner(
    input: String
) : Scanner<JsonTokenType>(
    input,
    JsonTokenType.EOF,
    RegularExpressions.WHITESPACE to null,
    RegularExpressions.JSON_NUMBER to JsonTokenType.NUMBER,
    RegularExpressions.DOUBLE_QUOTED_STRING to JsonTokenType.STRING,
    Regex.fromLiteral("true") to JsonTokenType.TRUE,
    Regex.fromLiteral("false") to JsonTokenType.FALSE,
    Regex.fromLiteral("null") to JsonTokenType.NULL,
    Regex.fromLiteral(",") to JsonTokenType.COMMA,
    Regex.fromLiteral("[") to JsonTokenType.ARRAY_START,
    Regex.fromLiteral("]") to JsonTokenType.ARRAY_END,
    Regex.fromLiteral("{") to JsonTokenType.OBJECT_START,
    Regex.fromLiteral("}") to JsonTokenType.OBJECT_END ,
    Regex.fromLiteral(":") to JsonTokenType.COLON
)