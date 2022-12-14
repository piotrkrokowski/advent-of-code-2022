class Puzzle13 : Puzzle<Int> {

    class Cursor {
        var position: Int = 0
    }

    sealed interface Signal : Comparable<Signal>

    data class SignalList(val value: List<Signal>) : Signal {
        override fun compareTo(other: Signal): Int {
            // println("Compare ${this} vs ${other}")
            return when (other) {
                is SingleSignal -> compareTo(SignalList(listOf(other)))
                is SignalList -> {
                    for (index in value.indices) {
                        if (index == other.value.size) return 1
                        val result = value[index].compareTo(other.value[index])
                        if (result != 0) return result
                    }
                    return if (other.value.size == value.size) 0 else -1
                }
            }
        }

        override fun toString(): String {
            return "$value"
        }

        companion object {
            fun fromString(string: String, cursor: Cursor = Cursor()): Signal {
                if (string[cursor.position] != '[') throw IllegalArgumentException(string)
                val values: MutableList<Signal> = mutableListOf()
                while (cursor.position++ < string.length - 1) {
                    when {
                        string[cursor.position].isDigit() -> {
                            values += SingleSignal.fromString(string, cursor)
                        }

                        string[cursor.position] == '[' -> {
                            @Suppress("RemoveRedundantQualifierName")
                            values += SignalList.fromString(string, cursor)
                        }

                        string[cursor.position] == ']' -> return SignalList(values)
                    }
                }
                throw IllegalArgumentException("Reached end of string - missing closing bracket? Position: ${cursor.position}")
            }
        }

    }

    data class SingleSignal(val value: Int) : Signal {
        override fun compareTo(other: Signal): Int {
            // println("Compare ${this} vs ${other}")
            if (other is SingleSignal) {
                return value.compareTo(other.value)
            } else {
                return SignalList(listOf(this)).compareTo(other)
            }
        }

        override fun toString(): String {
            return "$value"
        }

        companion object {
            fun fromString(string: String, cursor: Cursor): Signal {
                if (!string[cursor.position].isDigit()) throw IllegalArgumentException(string)
                var digits = ""
                while (string[cursor.position].isDigit()) {
                    digits += string[cursor.position++]
                }
                cursor.position--
                return SingleSignal(digits.toInt())
            }
        }


    }

    override fun solve(lines: List<String>): Int {
        var result = 0
        for (index in lines.indices step 3) {
            val line1 = SignalList.fromString(lines[index])
            val line2 = SignalList.fromString(lines[index + 1])
            val comparisonResult = line1.compareTo(line2)
            // println("$line1 vs $line2: $comparisonResult")
            if (comparisonResult == -1) {
                result += (index / 3) + 1
            }
        }
        return result
    }
}

fun main() {
    val result = Puzzle13().solveForFile()
    println("---")
    println(result)
}