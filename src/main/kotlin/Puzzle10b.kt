class Puzzle10b : Puzzle<String> {

    class Screen {
        private val screen: Array<CharArray> = Array(6) { CharArray(40) { ' ' } }
        private var cursorX = 0
        private var cursorY = 0

        fun drawPixel(register: Int) {
            val lit = register in ((cursorX - 1)..(cursorX + 1))
            screen[cursorY][cursorX] = if (lit) '#' else '.'
            cursorX++
            if (cursorX == 40) {
                cursorX = 0
                cursorY++
            }
            // println(renderScreen())
        }

        fun renderScreen() = screen.map { String(it) }.joinToString("\n").trim()
    }

    val finalStates: MutableList<Int> = mutableListOf(1)

    override fun solve(lines: List<String>): String {
        for (line in lines) {
            processCommands(line)
        }
        val screen = Screen()
        for (state in finalStates.dropLast(1)) {
            screen.drawPixel(state)
        }

        return screen.renderScreen()
    }


    private fun processCommands(line: String) {
        if (line == "noop") noop()
        else if (line.startsWith("addx")) {
            val diff = line.substring(5).toInt()
            noop()
            finalStates.add(getLatestState() + diff)
        }
    }

    private fun noop() {
        finalStates.add(getLatestState())
    }

    private fun getLatestState() = finalStates[finalStates.size - 1]

}


fun main() {
    val result = Puzzle10b().solveForFile()
    println("---")
    println(result)
}
