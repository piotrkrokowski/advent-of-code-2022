open class Puzzle6 : Puzzle<Int> {

    class Sequence(private val expectedLenght: Int) {
        private var string: String = ""
        private val occurrences: MutableMap<Char, Int> = HashMap()
        private var isUnique: Boolean = true

        fun addCharacter(newCharacter: Char) {
            if (string.length == expectedLenght) {
                val firstChar = string[0]
                occurrences[firstChar] = occurrences[firstChar]!! - 1
                string = string.substring(1)
            }
            occurrences[newCharacter] = (occurrences[newCharacter] ?: 0) + 1
            string += newCharacter
            isUnique = occurrences.values.all { it <= 1 }
        }

        fun meetsCriteria(): Boolean {
            return string.length == expectedLenght && isUnique
        }

        override fun toString(): String {
            return "Sequence(string=$string, occurences=${occurrences}, isUnique=$isUnique)"
        }
    }

    override fun solve(lines: List<String>): Int {
        return lines.map { findFirstUniqueSequence(it) }.sum()
    }

    private fun findFirstUniqueSequence(line: String): Int {
        val seq = Sequence(getExpectedLength())
        for (index in line.indices) {
            seq.addCharacter(line[index])
            // println(seq)
            if (seq.meetsCriteria()) return index + 1
        }
        throw IllegalArgumentException("Couldn't find start sequence")
    }

    open fun getExpectedLength() = 4

}

fun main() {
    val result = Puzzle6().solveForFile()
    println("---")
    println(result)
}

