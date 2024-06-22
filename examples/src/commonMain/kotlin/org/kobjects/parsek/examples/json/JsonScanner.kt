package org.kobjects.parsek.examples.json

import org.kobjects.parsek.tokenizer.Lexer
import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Scanner

class JsonScanner(
    input: String
) : Scanner<JsonTokenType>(
    Lexer(
        input,
    RegularExpressions.WHITESPACE to { null },
        RegularExpressions.NUMBER to { JsonTokenType.NUMBER },
        Regex("true") to { JsonTokenType.TRUE },
        Regex("false") to { JsonTokenType.FALSE },
        Regex("null") to { JsonTokenType.NULL },
        Regex(",") to { JsonTokenType.COMMA },
        Regex("\\[") to { JsonTokenType.ARRAY_START },
        Regex("]") to { JsonTokenType.ARRAY_END },
        Regex("\\{") to { JsonTokenType.OBJECT_START },
        Regex("}") to { JsonTokenType.OBJECT_END },
        Regex(":") to { JsonTokenType.COLON },
        RegularExpressions.DOUBLE_QUOTED_STRING to { JsonTokenType.STRING }),
    JsonTokenType.EOF
)