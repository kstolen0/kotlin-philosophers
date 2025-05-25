package org.example

import java.util.Date

fun main() {
    println("hello world")
}

suspend fun withTiming(action: suspend () -> Unit) {
    val start = Date().time
    action()
    println("Completed in ${(Date().time - start) / 1000} seconds")
}

