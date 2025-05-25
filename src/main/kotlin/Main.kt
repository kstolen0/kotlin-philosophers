package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date

typealias Chopstick = Mutex

var dumplings = 100

suspend fun main() {
    val ch1 = Mutex(false)
    val ch2 = Mutex(false)
    val ch3 = Mutex(false)

    val waiter = Waiter()
    val ph1 = Philosopher("Socrates", ch1, ch2, waiter)
    val ph2 = Philosopher("Albert", ch2, ch3, waiter)
    val ph3 = Philosopher("Soren", ch3, ch1, waiter)

    withTiming {
    coroutineScope {
            launch { ph1.eat() }
            launch { ph2.eat() }
            launch { ph3.eat() }
        }
    }
}

suspend fun withTiming(action: suspend () -> Unit) {
    val start = Date().time
    action()
    println("Completed in ${(Date().time - start) / 1000} seconds")
}

class Waiter() {
    val attention = Mutex(false)
    suspend fun requestChopsticks(left: Chopstick, right: Chopstick, action: suspend () -> Unit) {
        attention.withLock {
            left.withLock {
                right.withLock {
                    action()
                }
            }
        }
    }
}

class Philosopher(val name: String, val left: Chopstick, val right: Chopstick, val waiter: Waiter) {

    suspend fun eat() {
        while (dumplings > 0) {
            waiter.requestChopsticks(left, right) {
                if (dumplings > 0) {
                    println("$name eating dumpling")
                    dumplings--
                    println("$dumplings dumplings left")
                }
            }
            println("$name thinking")
            delay(100)
        }
    }
}