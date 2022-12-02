import java.io.File

class Puzzle1(private var lines: List<String>) {
    var max: Int = 0

    fun solve() {
        var sum = 0
        lines.forEach { line ->
            if (line.isEmpty()) {
                if (sum > max) {
                    max = sum
                }
                sum = 0
            } else {
                sum += line.toInt()
            }
        }
    }
}

fun main() {
    val lines = File("src/main/resources/puzzle1.txt").readLines()
    val puzzle = Puzzle1(lines)
    puzzle.solve()
    println("---")
    println(puzzle.max)
}