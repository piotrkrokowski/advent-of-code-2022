class Puzzle3 : Puzzle<Int> {
    override fun solve(lines: List<String>): Int {
        return lines
            .map { findDuplicate(it) }
            .map { calculatePriority(it) }
            // .onEach { println(it) }
            .sum()
    }

    private fun findDuplicate(line: String): Char {
        val part1 = line.substring(0, line.length / 2)
        val part2 = line.substring(line.length / 2)
        val foundCharacters: MutableSet<Char> = HashSet()
        part1.forEach { foundCharacters.add(it) }
        for (char in part2) {
            if (char in foundCharacters) return char
        }
        throw IllegalStateException("No duplicates found - invalid data: $line")
    }

    private fun calculatePriority(character: Char): Int {
        return if (character.isUpperCase())
            character.code - 'A'.code + 26 + 1
        else
            character.code - 'a'.code + 1
    }
}

fun main() {
    val result = Puzzle4().solveForFile()
    println("---")
    println(result)
}

