package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date

typealias Chopstick = Mutex

var dumplings = 500

fun main() {
    val ch1 = Mutex(false)
    val ch2 = Mutex(false)
    val ch3 = Mutex(false)
    val ch4 = Mutex(false)

    val ph1 = PolitePhilosopher("Aristotle", ch1, ch2)
    val ph2 = PolitePhilosopher("Jean", ch2, ch1)

    runBlocking {
        withTiming {
            val p1 = launch { ph1.eat() }
            val p2 = launch { ph2.eat() }

            p1.join()
            p2.join()
        }
    }
}

class PolitePhilosopher(val name: String, val left: Chopstick, val right: Chopstick) {
    suspend fun eat() {
        while (dumplings > 0) {
            if (left.tryLock()) {
                delay(1)
                println("$name picked up left ch")
                if (right.tryLock()) {
                    delay(1)
                    println("$name picked up right ch")
                    if (dumplings > 0) {
                        dumplings--
                    }
                    right.unlock()
                    println("$name released right ch")
                }
                left.unlock()
                println("$name released left ch")
            }
            println("$dumplings dumplings left")
        }
    }
}

suspend fun withTiming(action: suspend () -> Unit) {
    val start = Date().time
    action()
    println("Completed in ${(Date().time - start) / 1000} seconds")
}