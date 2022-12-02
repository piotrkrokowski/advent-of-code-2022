class Puzzle1 : Puzzle<Int> {
    override fun solve(lines: List<String>): Int {
        var sum = 0
        var max = 0
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
        return max
    }
}

fun main() {
    val result = Puzzle1().solveForFile()
    println("---")
    println(result)
}