class Puzzle6b : Puzzle6() {
    override fun getExpectedLength(): Int {
        return 14
    }
}


fun main() {
    val result = Puzzle6b().solveForFile()
    println("---")
    println(result)
}