package org.kobjects.parserlib.examples.pl0.runtime

class GlobalContext(
    val write: (Int) -> Unit,
    val read: () -> Int
)