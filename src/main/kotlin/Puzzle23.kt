import Puzzle23.Direction.N
import kotlin.math.max
import kotlin.math.min

open class Puzzle23 : Puzzle<Int> {

    data class Coords(val x: Int, val y: Int) {
        fun lookaround(): Set<Coords> {
            return Direction.values().flatMap { it.fieldsToScan.invoke(this) }.toSet()
        }
    }

    enum class Direction(val fieldsToScan: (Coords) -> List<Coords>) {
        N({ listOf(Coords(it.x - 1, it.y - 1), Coords(it.x, it.y - 1), Coords(it.x + 1, it.y - 1)) }),
        S({ listOf(Coords(it.x - 1, it.y + 1), Coords(it.x, it.y + 1), Coords(it.x + 1, it.y + 1)) }),
        W({ listOf(Coords(it.x - 1, it.y - 1), Coords(it.x - 1, it.y), Coords(it.x - 1, it.y + 1)) }),
        E({ listOf(Coords(it.x + 1, it.y - 1), Coords(it.x + 1, it.y), Coords(it.x + 1, it.y + 1)) });

        fun next(): Direction {
            return valueAtIndexRounded(ordinal + 1)
        }

        fun valuesStartingFromThis(): List<Direction> {
            // why not ;]
            return listOf(
                valueAtIndexRounded(ordinal),
                valueAtIndexRounded(ordinal + 1),
                valueAtIndexRounded(ordinal + 2),
                valueAtIndexRounded(ordinal + 3)
            )
        }

        private fun valueAtIndexRounded(index: Int) = values()[(index) % 4]
    }

    class DirectionRotator {
        private var currentPreference: Direction = N
        var consideredDirections: List<Direction> = prepareConsideredDirections()
        fun roll() {
            currentPreference = currentPreference.next()
            consideredDirections = prepareConsideredDirections()
        }

        private fun prepareConsideredDirections() = currentPreference.valuesStartingFromThis()
    }

    class Elf(
        var coords: Coords,
        private var field: Field,
    ) {
        private var plannedMove: Coords? = null

        fun registerCurrentPositionInField() {
            field.positions[coords] = this
        }

        fun plan() {
            plannedMove = null
            val lookaround = coords.lookaround().filter { field.positions.containsKey(it) }
            if (lookaround.isEmpty()) return
            for (consideredDirection in field.directionRotator.consideredDirections) {
                if (canMove(consideredDirection, lookaround)) {
                    plannedMove = consideredDirection.fieldsToScan(coords)[1] // XD, hack :P
                    field.claims[plannedMove!!] = (field.claims[plannedMove] ?: 0) + 1
                    break
                }
            }
        }

        fun move() {
            if (plannedMove == null || field.claims[plannedMove]!! > 1) {
                // Call it off, move conflict
            } else {
                field.elvesMoved = true
                field.positions.remove(coords)
                coords = plannedMove!!
                registerCurrentPositionInField()
            }
        }

        private fun canMove(direction: Direction, lookaround: List<Coords>): Boolean {
            return !direction.fieldsToScan(coords).any { lookaround.contains(it) }
        }

    }

    class Field {
        val directionRotator = DirectionRotator()
        val positions: MutableMap<Coords, Elf> = HashMap()
        val claims: MutableMap<Coords, Int> = HashMap()
        var elvesMoved = false
        private val elves: MutableList<Elf> = mutableListOf()
        private var initialMaxX = 0
        private var initialMaxY = 0

        fun createElvesFromLine(y: Int, line: String) {
            initialMaxX = max(initialMaxX, line.length - 1)
            initialMaxY = max(initialMaxY, y)
            for (x in line.indices) {
                if (line[x] == '#') {
                    val elf = Elf(Coords(x, y), this)
                    elf.registerCurrentPositionInField()
                    elves.add(elf)
                }
            }
        }

        fun executeRound() {
            elvesMoved = false
            claims.clear()
            elves.forEach { it.plan() }
            elves.forEach { it.move() }
            directionRotator.roll()
        }

        private fun findRectangle(): Pair<Coords, Coords> {
            var minX: Int = Int.MAX_VALUE
            var minY: Int = Int.MAX_VALUE
            var maxX: Int = Int.MIN_VALUE
            var maxY: Int = Int.MIN_VALUE
            positions.forEach {
                minX = min(minX, it.key.x)
                minY = min(minY, it.key.y)
                maxX = max(maxX, it.key.x)
                maxY = max(maxY, it.key.y)
            }
            return Pair(Coords(minX, minY), Coords(maxX, maxY))
        }

        fun getScore(): Int {
            val rectangle = findRectangle()
            var result = 0
            for (y in rectangle.first.y..rectangle.second.y) {
                for (x in rectangle.first.x..rectangle.second.x) {
                    if (!positions.containsKey(Coords(x, y))) result++

                }
            }
            return result
        }

        override fun toString(): String {
            val rectangle = findRectangle()
            return toString(rectangle)
        }

        fun toStringForInitialSize(): String {
            return toString(Pair(Coords(0, 0), Coords(initialMaxX, initialMaxY)))
        }

        private fun toString(rectangle: Pair<Coords, Coords>): String {
            val sb = StringBuilder()
            for (y in rectangle.first.y..rectangle.second.y) {
                for (x in rectangle.first.x..rectangle.second.x) {
                    if (positions.containsKey(Coords(x, y)))
                        sb.append('#')
                    else
                        sb.append('.')
                }
                sb.appendLine()
            }
            return sb.toString()
        }
    }

    override fun solve(lines: List<String>): Int {
        val field = initialize(lines)
        for (round in 1..10) {
            println("Round $round")
            field.executeRound()
            // println(field.toString())
        }
        return field.getScore()
    }

    fun initialize(lines: List<String>): Field {
        val field = Field()
        for (y in lines.indices) {
            field.createElvesFromLine(y, lines[y])
        }
        return field
    }
}

fun main() {
    val result = Puzzle23().solveForFile()
    println("---")
    println(result)
}