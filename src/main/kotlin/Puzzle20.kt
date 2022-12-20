import kotlin.math.abs

class Puzzle20 : Puzzle<Int> {

    data class Element(val value: Int, var moved: Boolean = false)

    class Solver(lines: List<Int>, val dontMoveHigherThan: Int? = null) {
        private var array: Array<Element>
        private val size: Int

        companion object {
            fun fromLines(lines: List<String>): Solver {
                return Solver(lines.map { it.toInt() })
            }
        }

        init {
            array = lines.map { Element(it.toInt()) }.toTypedArray()
            size = array.size
        }

        fun solve(): Int {
            // render()
            var index = 0
            while (index < array.size) {
                val element = array[index]
                if (element.value == 0 || element.moved || (dontMoveHigherThan != null && element.value > dontMoveHigherThan)) {
                    index++
                    continue
                }
                var newPosition = index + element.value
                val insertAfter = (element.value > 0)
                if (newPosition >= size) {
                    // Figuring it was a nightmare... I must admit, I suck at establishing indexing corner cases :(
                    val fullRounds = (element.value - 1) / (size - 1)
                    newPosition = (newPosition + fullRounds) % size
                } else if (newPosition < 0) {
                    val fullRounds = (abs(element.value) - 1) / (size - 1)
                    newPosition = size + ((newPosition - fullRounds) % size)
                }
                if (newPosition > index) newPosition--
                if (insertAfter) newPosition++
                array = array.copyOfRange(0, index) + array.copyOfRange(index + 1, array.size)
                array =
                    array.copyOfRange(0, newPosition) + arrayOf(element) + array.copyOfRange(newPosition, array.size)
                element.moved = true
                // render()
            }
            // render()
            return getAfterZero(1000) + getAfterZero(2000) + getAfterZero(3000)
        }

        fun getNumbers(): Array<Int> {
            return array.map { it.value }.toTypedArray()
        }

        private fun getAfterZero(indexAfterZero: Int): Int {
            val indexOfZero: Int = array.indexOfFirst { it.value == 0 }
            return array[(indexOfZero + indexAfterZero) % size].value
        }

        private fun render() {
            println(array.toList().map { it.value })
        }


    }

    override fun solve(lines: List<String>): Int {
        return Solver.fromLines(lines).solve()
    }
}

fun main() {
    val result = Puzzle20().solveForFile()
    println("---")
    println(result)
}