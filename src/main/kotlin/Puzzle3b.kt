class Puzzle3b : Puzzle<Int> {

    // Yeah, I know I could have made it based on some nerdy bit operations, but I didn't want to :P
    private class CharacterPresenceInBackpackGroup {
        var presence: Array<Boolean> = arrayOf(false, false, false)

        fun recordPresenceForLine(backpackIndex: Int): CharacterPresenceInBackpackGroup {
            presence[backpackIndex] = true
            return this
        }

        fun isPresentInAllThreeLines(): Boolean {
            return presence.all { it }
        }
    }

    private class Accumulator {
        var sumOfAllPriorities: Int = 0
        private var backpackInGroupIndex: Int = 0
        private val characterPresenceInBackpackGroupMap: MutableMap<Char, CharacterPresenceInBackpackGroup> = HashMap()

        fun addBackpack(line: String) {
            line.forEach {
                characterPresenceInBackpackGroupMap[it] = characterPresenceInBackpackGroupMap
                    .getOrPut(it) { CharacterPresenceInBackpackGroup() }
                    .recordPresenceForLine(backpackInGroupIndex)
            }
        }

        fun nextBackpack() {
            backpackInGroupIndex++
            if (backpackInGroupIndex > 2) {
                backpackInGroupIndex = 0
                val commonCharacterForAllThreeLines: Char =
                    characterPresenceInBackpackGroupMap.filter { it.value.isPresentInAllThreeLines() }.keys.first()
                characterPresenceInBackpackGroupMap.clear()
                sumOfAllPriorities += calculatePriority(commonCharacterForAllThreeLines)
            }
        }

        private fun calculatePriority(character: Char): Int {
            return if (character.isUpperCase())
                character.code - 'A'.code + 26 + 1
            else
                character.code - 'a'.code + 1
        }
    }

    override fun solve(lines: List<String>): Int {
        val result: Accumulator = lines.fold(Accumulator(), fun(accumulator, line): Accumulator {
            accumulator.addBackpack(line)
            accumulator.nextBackpack()
            return accumulator
        })
        return result.sumOfAllPriorities
    }
}

fun main() {
    val result = Puzzle3b().solveForFile()
    println("---")
    println(result)
}

