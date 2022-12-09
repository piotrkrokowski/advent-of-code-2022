import kotlin.math.max

class Puzzle8 : Puzzle<Int> {

    companion object {
        var counter: Long = 0
    }

    // It was supposed to be linear...
    // In reality, it's more like n*log(n) though. Still better than expected ;]
    // Next time - just go with square.
    // Refactoring note: It would look so much nicer if I introduced something like
    // enum Direction and not duplicate all of these methods :(
    // I think the time-competing formula of AoC contradicts a bit with the purpose of learning new language
    // and experimenting with its features.
    // Maybe I shall refactor it one day.
    class Tree(
        private val height: Int,
        private val row: Int,
        private val col: Int,
        private val forest: Forest
    ) {
        private var maxLeft: Int? = null
        private var maxRight: Int? = null
        private var maxTop: Int? = null
        private var maxBottom: Int? = null

        fun isVisible(): Boolean {
            return isEdge() || height > maxLeft() || height > maxRight() || height > maxTop() || height > maxBottom()
        }

        private fun isEdge(): Boolean {
            return row == 0 || col == 0 || row == forest.height - 1 || col == forest.width - 1
        }

        private fun maxLeft(): Int {
            if (maxLeft == null) maxLeft = calcMaxLeft()
            return maxLeft!!
        }

        private fun maxRight(): Int {
            if (maxRight == null) maxRight = calcMaxRight()
            return maxRight!!
        }

        private fun maxTop(): Int {
            if (maxTop == null) maxTop = calcMaxTop()
            return maxTop!!
        }

        private fun maxBottom(): Int {
            if (maxBottom == null) maxBottom = calcMaxBottom()
            return maxBottom!!
        }

        private fun maxLeftIncludingThis(): Int {
            return max(maxLeft(), height)
        }

        private fun maxRightIncludingThis(): Int {
            return max(maxRight(), height)
        }

        private fun maxTopIncludingThis(): Int {
            return max(maxTop(), height)
        }

        private fun maxBottomIncludingThis(): Int {
            return max(maxBottom(), height)
        }

        private fun calcMaxLeft(): Int {
            return if (col > 0) forest.at(row, col - 1).maxLeftIncludingThis() else 0
        }

        private fun calcMaxRight(): Int {
            return if (col < forest.width - 1) forest.at(row, col + 1).maxRightIncludingThis() else 0
        }

        private fun calcMaxTop(): Int {
            return if (row > 0) forest.at(row - 1, col).maxTopIncludingThis() else 0
        }

        private fun calcMaxBottom(): Int {
            return if (row < forest.height - 1) forest.at(row + 1, col).maxBottomIncludingThis() else 0
        }
    }

    class Forest(val width: Int, val height: Int) {
        private val forest: Array<Array<Tree?>> = Array(height) { Array(width) { null } }

        fun at(row: Int, col: Int): Tree {
            counter++
            return forest[row][col]!!
        }

        fun plant(row: Int, col: Int, tree: Tree) {
            forest[row][col] = tree
        }

        fun getNumberOfVisibleTrees(): Int {
            return forest.flatten().map { if (it!!.isVisible()) 1 else 0 }.sum()
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

        val result = forest.getNumberOfVisibleTrees()
        print("Operations count: $counter")
        return result
    }

}

fun main() {
    val result = Puzzle8().solveForFile()
    println("---")
    println(result)
}

