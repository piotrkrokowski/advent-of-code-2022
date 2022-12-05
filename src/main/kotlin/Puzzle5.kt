open class Puzzle5 : Puzzle<String> {

    class Stacks {
        private var stacks: MutableMap<Int, ArrayDeque<Char>> = HashMap()

        fun getFromTop(stackIndex: Int): Char {
            return getStack(stackIndex).removeFirst()
        }

        fun addOnTop(stackIndex: Int, crate: Char) {
            getStack(stackIndex).addFirst(crate)
        }

        fun addOnBottom(stackIndex: Int, crate: Char) {
            getStack(stackIndex).addLast(crate)
        }

        fun addFromLine(line: String) {
            val numberOfStacks: Int = (line.length + 1) / 4
            for (i in 1..numberOfStacks) {
                val characterAtPosition = line[(i - 1) * 4 + 1]
                if (characterAtPosition != ' ')
                    addOnBottom(i, characterAtPosition)
            }
        }

        fun getTopCrateFromEveryStack(): String {
            return stacks.keys.sorted().map { stacks[it]!!.first() }.joinToString("")
        }

        private fun getStack(stackIndex: Int): ArrayDeque<Char> {
            return stacks.getOrPut(stackIndex) { ArrayDeque() }
        }

        override fun toString(): String {
            return stacks.keys.sorted().map {
                "Stack ${it}: TOP ${stacks[it]} BOTTOM"
            }.joinToString("\n")
        }
    }

    open class Move(val quantity: Int, val from: Int, val to: Int) {
        open fun applyTo(stacks: Stacks) {
            repeat(quantity) {
                stacks.addOnTop(to, stacks.getFromTop(from))
            }
        }

        override fun toString(): String {
            return "Move $quantity from $from to $to"
        }


        companion object {
            private val pattern: Regex = Regex("move (\\d+) from (\\d+) to (\\d+)")
            fun fromLine(line: String): Move {
                val found = pattern.find(line)
                return Move(found!!.groupValues[1].toInt(), found.groupValues[2].toInt(), found.groupValues[3].toInt())
            }
        }
    }


    override fun solve(lines: List<String>): String {
        val stacks = Stacks()
        lines
            .filter { it.contains("[") }
            .forEach { stacks.addFromLine(it) }
        // println(stacks)
        lines
            .filter { it.startsWith("move") }
            .map { mapToMove(it) }
            .forEach { move ->
                // println(move);
                move.applyTo(stacks)
                // println(stacks)
            }

        return stacks.getTopCrateFromEveryStack()
    }

    open fun mapToMove(it: String) = Move.fromLine(it)
}

fun main() {
    val result = Puzzle5().solveForFile()
    println("---")
    println(result)
}

