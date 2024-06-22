package org.kobjects.parsek.examples.json

enum class JsonTokenType {
    NUMBER,
    STRING,
    TRUE,
    FALSE,
    NULL,
    ARRAY_START,
    ARRAY_END,
    OBJECT_START,
    OBJECT_END,
    COMMA,
    COLON,
    EOF
}