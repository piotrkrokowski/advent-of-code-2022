class Puzzle4 : Puzzle<Int> {

    data class Range(val start: Int, val finish: Int) {
        fun fullyContains(anotherRange: Range): Boolean {
            return start <= anotherRange.start && finish >= anotherRange.finish
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
            return if (isOneOfPairsFullyContainedInTheOther()) 1 else 0
        }

        private fun isOneOfPairsFullyContainedInTheOther(): Boolean {
            return range1.fullyContains(range2) || range2.fullyContains(range1)
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
    val result = Puzzle4().solveForFile()
    println("---")
    println(result)
}

