class Puzzle1b : Puzzle<Int> {
    private var topThree: MutableList<Int> = mutableListOf(0, 0, 0)
    private var minOfTopThree: Int = 0

    override fun solve(lines: List<String>): Int {
        var sum = 0
        lines.forEach { line ->
            if (line.isEmpty()) {
                maybePromote(sum)
                sum = 0
            } else {
                sum += line.toInt()
            }
        }
        maybePromote(sum)
        return result()
    }

    private fun maybePromote(sum: Int) {
        if (sum > minOfTopThree) promote(sum)
    }

    private fun result(): Int {
        return topThree.sum()
    }

    private fun promote(sum: Int) {
        topThree.remove(minOfTopThree)
        topThree.add(sum)
        minOfTopThree = topThree.min()
    }
}

fun main() {
    val result = Puzzle1b().solveForFile()
    println("---")
    println(result)
}