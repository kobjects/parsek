package org.kobjects.parserlib.playground

import org.kobjects.parserlib.tokenizer.RegularExpressions
import org.kobjects.parserlib.tokenizer.Tokenizer

val exampleCode = """
    def if(
        condition: () -> Boolean,
        body: () -> Unit, 
        vararg elif: ColonPair<() -> Boolean>, () -> Unit>,
        else: () -> Unit)
        
    def while(condition: Pair<() -> Boolean, () -> Unit>)
        
    def do(body: () -> Unit, while: () -> Boolean)    
    
    
""".trimIndent()


fun parse(tokenizer: TantillaTokenizer) {
    tokenizer.next()
    when (tokenizer.current.value) {
        "def" -> parseFunction(tokenizer)

    }


}

fun parseFunction(tokenizer: TantillaTokenizer) {
    tokenizer.consume("def")
    val functionName = tokenizer.consume(TantillaToken.IDENTIFIER)
    tokenizer.consume("(")
    val parameters = mutableListOf<Parameter>()
    if (!tokenizer.tryConsume(")")) {
        do {
            val parameterName = tokenizer.consume(TantillaToken.IDENTIFIER)
            tokenizer.consume(":")
            val parameterType = parseType(tokenizer)
            parameters.add(Parameter(parameterName, parameterType))
        } while (tokenizer.tryConsume(","))
        tokenizer.consume(")")
    }



}

data class Parameter(
    val name: String,
    val type: Type
)

open class Type {

}

class PrimitiveType(val nam: String) : Type() {

    companion object {
        val STRING = PrimitiveType("String")
        val NUMBER = PrimitiveType("Number")
    }
}



fun parseType(tokenizer: TantillaTokenizer): Type {
    val name = tokenizer.consume(TantillaToken.IDENTIFIER)
    return when (name) {
        "String" -> PrimitiveType.STRING
        "Number" -> PrimitiveType.NUMBER
        else -> throw IllegalArgumentException("Unrecognized type: $name")
    }
}



enum class TantillaToken {
    BOF, SYMBOL, STRING, NUMBER, IDENTIFIER, EOF
}


class TantillaTokenizer(input: String) : Tokenizer<TantillaToken>(
    bofType = TantillaToken.BOF,
    listOf(
        RegularExpressions.NUMBER to TantillaToken.NUMBER,
        RegularExpressions.IDENTIFIER to TantillaToken.IDENTIFIER,
        RegularExpressions.SYMBOL to TantillaToken.SYMBOL,
        RegularExpressions.WHITESPACE to null,
        RegularExpressions.STRING to TantillaToken.STRING
    ),
    eofType =  TantillaToken.EOF,
    input
)






