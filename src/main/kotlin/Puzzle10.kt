class Puzzle10 : Puzzle<Int> {

    val finalStates: MutableList<Int> = mutableListOf(1)

    override fun solve(lines: List<String>): Int {
        for (line in lines) {
            if (line == "noop") noop()
            else if (line.startsWith("addx")) {
                val diff = line.substring(5).toInt()
                noop()
                finalStates.add(finalStates[finalStates.size - 1] + diff)
            }
        }
        return listOf(20, 60, 100, 140, 180, 220).map { finalStates.get(it - 1) * it }.sum()
    }

    private fun noop() {
        finalStates.add(finalStates.get(finalStates.size - 1))
    }

}

fun main() {
    val result = Puzzle10().solveForFile()
    println("---")
    println(result)
}
