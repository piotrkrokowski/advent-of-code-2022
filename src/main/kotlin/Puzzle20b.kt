@file:Suppress("DuplicatedCode")

import java.util.*
import kotlin.math.abs

class Puzzle20b : Puzzle<Long> {

    // The wrapper class is to enforce uniqueness of number-objects in array
    class Element(val value: Long) {
        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }

    class Solver(lines: List<Long>, private val dontMoveHigherThan: Int? = null) {
        private var array: Array<Element>
        private var originalOrder: Array<Element>
        private val size: Int

        companion object {
            fun fromLines(lines: List<String>): Solver {
                return Solver(lines.map { it.toLong() * 811589153 })
            }
        }

        init {
            originalOrder = lines.map { Element(it) }.toTypedArray()
            array = originalOrder.copyOf()
            size = array.size
        }

        fun mix() {
            // render()
            for (originalElement in originalOrder) {
                val index = array.indexOf(originalElement)
                val element = array[index]
                if (element.value == 0L || (dontMoveHigherThan != null && element.value > dontMoveHigherThan)) {
                    continue
                }
                val insertAfter = (element.value > 0)
                var newPosition = wrapNewPosition(index + element.value, element)
                if (newPosition > index) newPosition--
                if (insertAfter) newPosition++
                array = array.copyOfRange(0, index) + array.copyOfRange(index + 1, array.size)
                array =
                    array.copyOfRange(0, newPosition) + arrayOf(element) + array.copyOfRange(newPosition, array.size)
                // render()
            }
            // render()
        }

        private fun wrapNewPosition(newPositionWithOverflow: Long, element: Element): Int {
            if (newPositionWithOverflow >= size) {
                // Figuring it was a nightmare... I must admit, I suck at establishing indexing corner cases :(
                val fullRounds = (element.value - 1) / (size - 1)
                return ((newPositionWithOverflow + fullRounds) % size).toInt()
            } else if (newPositionWithOverflow < 0) {
                val fullRounds = (abs(element.value) - 1) / (size - 1)
                return (size + ((newPositionWithOverflow - fullRounds) % size)).toInt()
            } else return newPositionWithOverflow.toInt()
        }

        fun solve(): Long {
            repeat(10) { mix() }
            return getAfterZero(1000) + getAfterZero(2000) + getAfterZero(3000)
        }

        fun getNumbers(): Array<Long> {
            return array.map { it.value }.toTypedArray()
        }

        private fun getAfterZero(indexAfterZero: Int): Long {
            val indexOfZero: Int = array.indexOfFirst { it.value == 0L }
            return array[(indexOfZero + indexAfterZero) % size].value
        }

        private fun render() {
            println(array.toList().map { it.value })
        }
    }

    override fun solve(lines: List<String>): Long {
        return Solver.fromLines(lines).solve()
    }
}

fun main() {
    val result = Puzzle20b().solveForFile()
    println("---")
    println(result)
}