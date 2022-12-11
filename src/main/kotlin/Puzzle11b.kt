class Puzzle11b : Puzzle<Long> {

    class Monkey(
        val index: Int,
        private val items: ArrayDeque<Long> = ArrayDeque(),
        private val operation: (Long) -> Long,
        val divisibleBy: Long,
        private val nextMonkeyOnTrue: Int,
        private val nextMonkeyOnFalse: Int,
        private val monkeys: List<Monkey>
    ) {
        var counter: Long = 0

        fun doTurn(hash: Long) {
            while (!items.isEmpty()) {
                counter++
                var item = items.removeFirst()
                // I was so close at the beginning :( Instead of calculacing the remainder I've left the result...
                // Then I drifted off in completely different direction, trying to calculate factors manually, etc, and got lost entirely
                // Yeah, maths was never my strong point :/
                item %= hash
                item = operation.invoke(item)
                val nextMonkey = if (item % divisibleBy == 0L) nextMonkeyOnTrue else nextMonkeyOnFalse
                monkeys[nextMonkey].items.addLast(item)
            }
        }
    }


    override fun solve(lines: List<String>): Long {
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
            .map { it ->
                Monkey(
                    it.groupValues[1].trim().toInt(),
                    ArrayDeque(it.groupValues[2].split(",").toList().map { num -> num.trim().toLong() }),
                    mapOperation(it),
                    it.groupValues[5].trim().toLong(),
                    it.groupValues[6].trim().toInt(),
                    it.groupValues[7].trim().toInt(),
                    monkeys
                )
            }
            .onEach { println(it) }
            .forEach { monkeys.add(it) }


        val hash = monkeys.map { it.divisibleBy }.reduce { acc, it -> acc * it }

        for (round in 1..10_000) {
            monkeys.forEach { it.doTurn(hash) }
            if (round == 20 || round % 1000 == 0) {
                println("After round $round")
                monkeys.forEach {
                    println("" + it.index + ": " + it.counter)
                }
            }
        }

        val topTwo = monkeys.map { it.counter }.sortedDescending().take(2)

        return topTwo[0] * topTwo[1]
    }

    private fun mapOperation(it: MatchResult): (Long) -> Long {
        return if (it.groupValues[3].trim() == "+")
            fun(v: Long): Long { return v + mapArgument(v, it.groupValues[4]) }
        else
            fun(v: Long): Long { return v * mapArgument(v, it.groupValues[4]) }
    }

    private fun mapArgument(v: Long, argument: String): Long {
        return if (argument == "old") v else argument.toLong()
    }
}

fun main() {
    val result = Puzzle11b().solveForFile()
    println("---")
    println(result)
}
