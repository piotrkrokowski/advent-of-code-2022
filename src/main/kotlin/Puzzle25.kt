import java.math.BigInteger

class Puzzle25 : Puzzle<String> {
    override fun solve(lines: List<String>): String {
        val sum: BigInteger = lines
            .map { SnafuNumber(it).toDecimal() }
            .onEach { println(it) }
            .reduce { acc, next -> acc.plus(next) }

        println("Sum: $sum")
        return SnafuNumber.fromDecimal(sum).value
    }
}

fun main() {
    val result = Puzzle25().solveForFile()
    println("---")
    println(result)
}