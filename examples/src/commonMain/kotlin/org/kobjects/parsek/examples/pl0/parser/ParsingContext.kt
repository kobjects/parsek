package org.kobjects.parsek.examples.pl0.parser

/**
 * "symbols" contanins constants (mapped to an int) and variables (mapped to null)
 */
class ParsingContext(
    parentContext: ParsingContext?,
    symbols: Map<String, Int?>,
    procedureNames: Set<String>
) {
    val symbols: Map<String, Int?> = if (parentContext == null) symbols
    else parentContext.symbols.toMutableMap().apply { putAll( symbols) }.toMap()
    val procedureNames: Set<String> = if (parentContext == null) procedureNames
    else parentContext.procedureNames.toMutableSet().apply { addAll (procedureNames)}
}