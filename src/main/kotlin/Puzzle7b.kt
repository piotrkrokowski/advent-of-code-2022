const val FILESYSTEM_SIZE = 70000000
const val UPDATE_SIZE = 30000000

class Puzzle7b : Puzzle7() {

    override fun solve(lines: List<String>): Int {
        super.solve(lines)
        val currentSpace = FILESYSTEM_SIZE - root.cachedSize
        val spaceToFreeUp = UPDATE_SIZE - currentSpace
        val flattened = root.getSubdirectories()
        // print(flattened)
        return flattened
            .map { it.cachedSize }
            .filter { it >= spaceToFreeUp }
            .min()
    }

}

fun main() {
    val result = Puzzle7b().solveForFile()
    println("---")
    println(result)
}

