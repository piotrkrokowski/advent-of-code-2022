import java.io.File

class Puzzle1b(private var lines: List<String>) {
    var topThree: MutableList<Int> = mutableListOf(0, 0, 0)
    var minOfTopThree: Int = 0

    fun solve(): Int {
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

    fun result(): Int {
        return topThree.sum()
    }

    private fun promote(sum: Int) {
        topThree.remove(minOfTopThree)
        topThree.add(sum)
        minOfTopThree = topThree.min()
    }
}

fun main() {
    val lines = File("src/main/resources/puzzle1b.txt").readLines()
    val puzzle = Puzzle1b(lines)
    val result = puzzle.solve()
    println("---")
    println(result)
}