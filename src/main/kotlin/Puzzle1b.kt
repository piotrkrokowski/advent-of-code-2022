class Puzzle1b : Puzzle<Int> {

    private class State {
        var topThree: MutableList<Int> = mutableListOf(0, 0, 0)
        var minOfTopThree: Int = 0
    }

    override fun solve(lines: List<String>): Int {
        var sum = 0
        val state = State()
        for (line in lines) {
            if (line.isEmpty()) {
                maybePromote(sum, state)
                sum = 0
            } else {
                sum += line.toInt()
            }
        }
        maybePromote(sum, state)
        return result(state)
    }

    private fun maybePromote(sum: Int, state: State) {
        if (sum > state.minOfTopThree) promote(sum, state)
    }

    private fun result(state: State): Int {
        return state.topThree.sum()
    }

    private fun promote(sum: Int, state: State) {
        with(state) {
            topThree.remove(minOfTopThree)
            topThree.add(sum)
            minOfTopThree = topThree.min()
        }
    }
}

fun main() {
    val result = Puzzle1b().solveForFile()
    println("---")
    println(result)
}