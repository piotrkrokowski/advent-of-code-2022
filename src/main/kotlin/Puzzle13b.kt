class Puzzle13b : Puzzle13() {
    override fun solve(lines: List<String>): Int {
        val linesWithDividers = lines + "[[2]]" + "[[6]]"
        val sorted = linesWithDividers
            .filter { it.isNotBlank() }
            .map { SignalList.fromString(it) }
            .sorted()

        // println(sorted.joinToString("\n"))

        val index1 = sorted.indexOfFirst { it.toString() == "[[2]]" } + 1
        val index2 = sorted.indexOfFirst { it.toString() == "[[6]]" } + 1

        return index1 * index2
    }
}

fun main() {
    val result = Puzzle13b().solveForFile()
    println("---")
    println(result)
}
