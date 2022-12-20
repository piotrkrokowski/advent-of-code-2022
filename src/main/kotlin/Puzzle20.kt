import kotlin.math.max
import kotlin.math.min

class Puzzle20 : Puzzle<Int> {

    class Solver(lines: List<String>) {
        private val values: IntArray
        private val positions: IntArray
        private val size: Int
        private val indexOfZero: Int

        init {
            values = lines.map { it.toInt() }.toIntArray()
            positions = List(lines.size) { index -> index }.toIntArray()
            size = values.size
            indexOfZero = values.indexOf(0)
        }

        fun getAt(position: Int): Int {
            val positionOfZero = positions[indexOfZero]
            val positionToGet = (positionOfZero + position) % size
            val indexToGet = positions.indexOf(positionToGet)
            return values[indexToGet]
        }

        fun checkConsistency() {
            val actual = positions.toSortedSet().reversed()
            val expected = (0 until size).toSortedSet().reversed()
            if (!actual.equals(expected)) {
                val diff = expected - actual
                render()
                throw IllegalStateException("Diff: ${diff}")
            }
        }

        fun solve(): Int {
            // render()
            for (mainIndex in values.indices) {
                val value = values[mainIndex]
                if (value == 0) continue
                val currentPosition = positions[mainIndex]
                var newPosition = currentPosition + value
                if (newPosition >= size) {
                    newPosition = (newPosition % size) + 1
                } else if (newPosition <= 0) {
                    newPosition = size + ((newPosition - 1) % size)
                }
                positions[mainIndex] = newPosition
                renumberPositionsAfterMovingElement(mainIndex, currentPosition, newPosition)
                checkConsistency()
                // render()
            }
            render()
            return getAt(1000) + getAt(2000) + getAt(3000)
        }

        private fun render() {
            val sortedPairsPositionToValue = positions.mapIndexed { index, position -> position to index }
                .sortedBy { pair -> pair.first }
                .map { pair -> Pair(pair.first, values[pair.second]) }
            println(sortedPairsPositionToValue)
            val joined = sortedPairsPositionToValue.map { it.second }.joinToString(",")
            println(joined)
        }

        private fun renumberPositionsAfterMovingElement(movedElementIndex: Int, oldPosition: Int, newPosition: Int) {
            for (renumbered in values.indices) {
                if (renumbered == movedElementIndex) continue
                if (positions[renumbered] in (min(oldPosition, newPosition)..max(oldPosition, newPosition))) {
                    if (newPosition < oldPosition) {
                        positions[renumbered]++
                    } else {
                        positions[renumbered]--
                    }
                }
            }
        }
    }

    override fun solve(lines: List<String>): Int {
        return Solver(lines).solve()
    }
}

fun main() {
    val result = Puzzle20().solveForFile()
    println("---")
    println(result)
}