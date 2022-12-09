class Puzzle8b : Puzzle<Int> {

    companion object {
        var counter: Long = 0
    }

    // It turns out n^2 is good enough for AoC :(
    // Discovery of my life: First look at the input data and then pick the complexity
    class Tree(
        private val height: Int,
        private val row: Int,
        private val col: Int,
        private val forest: Forest
    ) {
        fun getScore(): Int {
            return vdLeft() * vdRight() * vdTop() * vdBottom()
        }

        private fun vdLeft(): Int {
            return calcVievDistance { it.left() }
        }

        private fun vdRight(): Int {
            return calcVievDistance { it.right() }
        }

        private fun vdTop(): Int {
            return calcVievDistance { it.top() }
        }

        private fun vdBottom(): Int {
            return calcVievDistance { it.bottom() }
        }

        private fun calcVievDistance(func: (Tree) -> Tree?): Int {
            var n: Tree? = func.invoke(this)
            var vd = 0
            while (n != null) {
                vd++
                if (n.height >= height) break
                n = func.invoke(n)
            }
            return vd
        }

        private fun left(): Tree? {
            return forest.at(row, col - 1)
        }

        private fun right(): Tree? {
            return forest.at(row, col + 1)
        }

        private fun top(): Tree? {
            return forest.at(row - 1, col)
        }

        private fun bottom(): Tree? {
            return forest.at(row + 1, col)
        }

        override fun toString(): String {
            return "(${row}, ${col}) vdLeft: ${vdLeft()}, vdRight: ${vdRight()}, vdTop: ${vdTop()}, vdBottom: ${vdBottom()}"
        }
    }

    class Forest(private val width: Int, private val height: Int) {
        private val forest: Array<Array<Tree?>> = Array(height) { Array(width) { null } }

        fun at(row: Int, col: Int): Tree? {
            counter++
            return if (row < 0 || col < 0 || row > height - 1 || col > width - 1)
                null
            else
                forest[row][col]
        }

        fun plant(row: Int, col: Int, tree: Tree) {
            forest[row][col] = tree
        }

        fun findBestTreeScore(): Int {
            return forest.flatten().map { it!!.getScore() }.max()
        }
    }

    override fun solve(lines: List<String>): Int {
        val forest = Forest(lines[0].length, lines.size)
        for (row in lines.indices) {
            val line = lines[row]
            for (col in line.indices) {
                val height: Int = line[col].digitToInt()
                forest.plant(row, col, Tree(height, row, col, forest))
            }
        }
        val result = forest.findBestTreeScore()
        print("Operations count: $counter")
        return result
    }

}

fun main() {
    val result = Puzzle8b().solveForFile()
    println("---")
    println(result)
}

