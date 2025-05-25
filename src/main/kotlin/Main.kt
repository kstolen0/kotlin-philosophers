package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date

var dumplings = 100

class Philosopher(val name: String, val leftChopstick: Mutex, val rightChopstick: Mutex) {
    suspend fun eat() {
        while (dumplings > 0) {
            leftChopstick.withLock {
                println("$name picked up left chopstick")
                delay(1)
                rightChopstick.withLock {
                    println("$name picked up right chopstick")
                    dumplings--
                }
                println("$name released left chopstick")
            }

            println("$name released right chopstick")
            println("$dumplings dumplings left")
            println("$name thinking")
            delay(100)
        }
    }
}

fun main() {

    val ch1 = Mutex(false)
    val ch2 = Mutex(false)
    val ch3 = Mutex(false)

    val p1 = Philosopher("Aristotle", ch1, ch2)
    val p2 = Philosopher("Rene", ch2, ch3)
    val p3 = Philosopher("Jean", ch3, ch1)

    runBlocking {
        withTiming {
            val eating1 = launch { p1.eat() }
            val eating2 = launch { p2.eat() }
            val eating3 = launch { p3.eat() }

            eating1.join()
            eating2.join()
            eating3.join()
        }
    }
}

suspend fun withTiming(action: suspend () -> Unit) {
    val start = Date().time
    action()
    println("completed in ${(Date().time - start) / 1000} seconds")
}