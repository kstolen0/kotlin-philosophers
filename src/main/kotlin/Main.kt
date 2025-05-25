package org.example

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import java.util.Date

typealias Chopstick = Mutex

var dumplings = 100

suspend fun main() {
    val ch1 = Mutex(false)
    val ch2 = Mutex(false)
    val ch3 = Mutex(false)

    val ph1 = PolitePhilosopher("Aristotle", ch1, ch2)
    val ph2 = PolitePhilosopher("Jean", ch2, ch3)
    val ph3 = PolitePhilosopher("Daniel", ch3, ch1)

    withTiming {
        coroutineScope {
            val ph1Job = async { ph1.eat() }
            val ph2Job = async { ph2.eat() }
            val ph3Job = async { ph3.eat() }

            println("${ph1.name} ate ${ph1Job.await()} dumplings")
            println("${ph2.name} ate ${ph2Job.await()} dumplings")
            println("${ph3.name} ate ${ph3Job.await()} dumplings")
        }
    }
}

class PolitePhilosopher(val name: String, val left: Chopstick, val right: Chopstick) {
    suspend fun eat(): Int {
        var dumplingsAte = 0
        while (dumplings > 0) {
            if (left.tryLock()) {
                println("$name picked up left ch")
                if (right.tryLock()) {
                    println("$name picked up right ch")
                    if (dumplings > 0) {
                        println("$name ate dumpling")
                        dumplings--
                        dumplingsAte++
                    }
                    right.unlock()
                    println("$name released right ch")
                }
                left.unlock()
                println("$name released left ch")
            }
            println("$dumplings dumplings left")
            println("$name thinking")
            delay(100)
        }
        return dumplingsAte
    }
}

suspend fun withTiming(action: suspend () -> Unit) {
    val start = Date().time
    action()
    println("Completed in ${(Date().time - start) / 1000} seconds")
}