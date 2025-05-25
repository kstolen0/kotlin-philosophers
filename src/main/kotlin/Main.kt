package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date

typealias Chopstick = Mutex

var dumplings = 100

suspend fun main() {

    val ch1 = Mutex(false)
    val ch2 = Mutex(false)

    val ph1 = Philosopher("Socrates", ch1, ch2)
    val ph2 = Philosopher("Rene", ch2, ch1)

    withTiming {
        coroutineScope {
            while (dumplings > 0) {
                ph1.eat()
                ph2.eat()
            }
        }
    }
}

suspend fun withTiming(action: suspend () -> Unit) {
    val start = Date().time
    action()
    println("Completed in ${(Date().time - start) / 1000} seconds")
}

class Philosopher(val name: String, val left: Chopstick, val right: Chopstick) {
    suspend fun eat() {
        left.withLock {
            println("$name picked up ch 1")
            right.withLock {
                println("$name picked up ch 2")
                if (dumplings > 0) {
                    dumplings--
                    println("$name ate dumpling")
                }
            }
            println("$name released ch 2")
        }
        println("$name released ch 1")
        println("$dumplings dumplings left")
        println("$name thinking")
        delay(100)
    }
}
