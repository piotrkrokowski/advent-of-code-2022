class Puzzle4b : Puzzle<Int> {

    data class Range(val start: Int, val finish: Int) {
        fun overlapsLeft(anotherRange: Range): Boolean {
            // return finish >= anotherRange.start && start <= anotherRange.start
            return anotherRange.start in start..finish // Kotlin way ;]
        }

        companion object {
            fun fromString(rangeString: String): Range {
                val indexes = rangeString.split("-").map { it.toInt() }
                return Range(indexes[0], indexes[1])
            }
        }
    }

    data class Pair(val range1: Range, val range2: Range) {
        fun value(): Int {
            return if (areOverlapping()) 1 else 0
        }

        private fun areOverlapping(): Boolean {
            val overlapsLeft = range1.overlapsLeft(range2)
            val overlapsRight = range2.overlapsLeft(range1)
//            println(this)
//            println(overlapsLeft)
//            println(overlapsRight)
            return overlapsLeft || overlapsRight
        }

        companion object {
            fun fromString(line: String): Pair {
                val ranges = line.split(",").map { Range.fromString(it) }
                return Pair(ranges[0], ranges[1])
            }
        }
    }

    override fun solve(lines: List<String>): Int {
        return lines.map { Pair.fromString(it) }.map { it.value() }.sum()
    }
}

fun main() {
    val result = Puzzle4b().solveForFile()
    println("---")
    println(result)
}

