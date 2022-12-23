class Puzzle23b : Puzzle23() {

    override fun solve(lines: List<String>): Int {
        val field = initialize(lines)
        var round = 0
        while (field.elvesMoved || round == 0) {
            round++
            println("Round $round")
            field.executeRound()
            // println(field.toString())
        }
        return round
    }
}

fun main() {
    val result = Puzzle23b().solveForFile()
    println("---")
    println(result)
}