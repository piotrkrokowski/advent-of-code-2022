import kotlin.math.floor

class Puzzle11 : Puzzle<Int> {

    class Monkey(
        val index: Int,
        val items: ArrayDeque<Int> = ArrayDeque(),
        private val operation: (Int) -> Int,
        private val divisibleBy: Int,
        private val nextMonkeyOnTrue: Int,
        private val nextMonkeyOnFalse: Int,
        private val monkeys: List<Monkey>
    ) {
        var counter: Int = 0

        fun doTurn() {
            while (!items.isEmpty()) {
                counter++
                var item = items.removeFirst()
                item = operation.invoke(item)
                item = floor(item / 3.0).toInt()
                val nextMonkey = if (item % divisibleBy == 0) nextMonkeyOnTrue else nextMonkeyOnFalse
                monkeys[nextMonkey].items.addLast(item)
            }
        }

        override fun toString(): String {
            return "Monkey(items=$items, operation=$operation, divisibleBy=$divisibleBy, nextMonkeyOnTrue=$nextMonkeyOnTrue, nextMonkeyOnFalse=$nextMonkeyOnFalse, counter=$counter)"
        }
    }

    override fun solve(lines: List<String>): Int {
        val singleString = lines.joinToString("\n")
        val regex = Regex(
            """
            Monkey (\d+):\s*
            \s*Starting items: ([0-9, ]+)\s*
            \s*Operation: new = old ([+*]) (old|\d+)\s*
            \s*Test: divisible by (\d+)\s*
            \s*If true: throw to monkey (\d+)\s*
            \s*If false: throw to monkey (\d+)\s*
        """.trimIndent()
        )
        val result = regex.findAll(singleString)
        val monkeys: MutableList<Monkey> = ArrayList()
        result
            .map {
                Monkey(
                    it.groupValues[1].trim().toInt(),
                    ArrayDeque(it.groupValues[2].split(",").toList().map { num -> num.trim().toInt() }),
                    mapOperation(it),
                    it.groupValues[5].trim().toInt(),
                    it.groupValues[6].trim().toInt(),
                    it.groupValues[7].trim().toInt(),
                    monkeys
                )
            }
            .onEach { println(it) }
            .forEach { monkeys.add(it) }

        for (round in 1..20) {
            monkeys.forEach { it.doTurn() }
            println("After round $round")
            monkeys.forEach {
                println("" + it.index + ": " + it.items)
            }
        }

        val topTwo = monkeys.map { it.counter }.sortedDescending().take(2)

        return topTwo[0] * topTwo[1]
    }

    private fun mapOperation(it: MatchResult): (Int) -> Int {
        return if (it.groupValues[3].trim() == "+")
            fun(v: Int): Int { return v + mapArgument(v, it.groupValues[4]) }
        else
            fun(v: Int): Int { return v * mapArgument(v, it.groupValues[4]) }
    }

    private fun mapArgument(v: Int, argument: String): Int {
        return if (argument == "old") v else argument.toInt()
    }
}

fun main() {
    val result = Puzzle11().solveForFile()
    println("---")
    println(result)
}
