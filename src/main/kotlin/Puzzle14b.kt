class Puzzle14b : Puzzle14() {
    override fun solve(lines: List<String>): Int {
        val cave = Cave.fromLines(lines)
        val floor = cave.maxY + 2
        cave.addRocks(Coords(0, floor), Coords(cave.width - 1, floor))
        // println(cave.draw())

        var result = 0
        while (cave.getAt(cave.sandSource) != Field.SAND) {
            result++
            cave.spawnSandBlock().drop()
            println(result)
            // println(cave.draw())
        }

        return result
    }
}

fun main() {
    val result = Puzzle14b().solveForFile()
    println("---")
    println(result)
}