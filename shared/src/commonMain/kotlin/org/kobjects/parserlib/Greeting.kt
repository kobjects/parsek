package org.kobjects.parserlib

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}