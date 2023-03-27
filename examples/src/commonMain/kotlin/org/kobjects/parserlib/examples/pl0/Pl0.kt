package org.kobjects.parserlib.examples.pl0

import org.kobjects.parserlib.examples.pl0.node.Program
import org.kobjects.parserlib.examples.pl0.parser.Pl0Scanner

fun parseProgram(text: String): Program =
    org.kobjects.parserlib.examples.pl0.parser.parseProgram(Pl0Scanner(text))

