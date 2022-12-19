import Puzzle17b.Direction.DOWN

class Puzzle17b(val iterations: Long) : Puzzle<Long> {

    companion object {
        const val CAVE_WIDTH = 7
        const val ITERATIONS = 1_000_000_000_000
    }

    enum class Direction(val vector: Coord) {
        LEFT(Coord(0, -1)), RIGHT(Coord(0, 1)), DOWN(Coord(-1, 0));

        companion object {
            fun fromChar(char: Char): Direction {
                return if (char == '<') LEFT else RIGHT
            }
        }
    }

    data class Coord(val y: Long, val x: Long) {
        fun move(direction: Direction): Coord {
            return move(direction.vector)
        }

        fun move(vector: Coord): Coord {
            return Coord(y + vector.y, x + vector.x)
        }
    }

    enum class Shape(val elements: List<Coord>) {
        LINE(listOf(Coord(0, 0), Coord(0, 1), Coord(0, 2), Coord(0, 3))),
        CROSS(listOf(Coord(0, 1), Coord(1, 0), Coord(1, 1), Coord(1, 2), Coord(2, 1))),
        L(listOf(Coord(0, 0), Coord(0, 1), Coord(0, 2), Coord(1, 2), Coord(2, 2))),
        I(listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0))),
        SQUARE(listOf(Coord(0, 0), Coord(0, 1), Coord(1, 0), Coord(1, 1)));

        companion object {
            fun nextShape(index: Long): Shape {
                return values()[(index % values().size).toInt()]
            }
        }
    }

    class Rock(val shape: Shape, var leftBottom: Coord, val cave: Cave) {
        private var solidified = false

        fun simulate() {
            while (!solidified) {
                val jetDirection = cave.getNextJetDirection()
                applyJetMove(jetDirection)
                applyDownMove()
            }
        }

        private fun applyJetMove(direction: Direction) {
            if (!doesCollideAfterMove(direction)) {
                doMove(direction)
            }
        }

        private fun applyDownMove() {
            if (doesCollideAfterMove(Direction.DOWN)) {
                solidify()
            } else {
                doMove(DOWN)
            }
        }

        private fun doMove(direction: Direction) {
            leftBottom = leftBottom.move(direction.vector)
        }

        private fun solidify() {
            getAllElementsCoords().forEach { cave.addSolidifiedRockElement(it) }
            solidified = true
        }

        private fun doesCollideAfterMove(direction: Direction): Boolean {
            val rockAfterMove = Rock(shape, leftBottom.move(direction.vector), cave)
            return rockAfterMove.getIsAnyElementOutOfBounds() || rockAfterMove.collidesWithRock()
        }

        private fun getIsAnyElementOutOfBounds(): Boolean {
            return getAllElementsCoords().any { it.x !in 0 until CAVE_WIDTH || it.y < 0 }
        }

        private fun collidesWithRock(): Boolean {
            return getAllElementsCoords().any { cave.isOccupied(it) }
        }

        private fun getAllElementsCoords(): List<Coord> {
            return shape.elements.map { leftBottom.move(it) }
        }
    }

    class Cave(jetsString: String) {
        val jetsSequence: List<Direction>
        var solidifiedRocksCoords: MutableSet<Coord> = mutableSetOf()
        var topMostElement = -1L
        var hiddenHeight = 0L
        var rocksCounter = 0L
        var jetsIndex = 0

        fun spawnRock(): Rock {
            if (jetsIndex == 0 && rocksCounter % 5 == 0L) {
                println("trap")
            }
            if (jetsIndex == 0) {
                println(Shape.nextShape(rocksCounter))
                println(rocksCounter)
            }

//            if (topMostElement > 5) reduce()
            // println("Spawning rock ${rocksCounter + 1}")
            val spawnCoords = Coord(topMostElement + 3 + 1, 2)
            val shape = Shape.nextShape(rocksCounter++)
            return Rock(shape, spawnCoords, this)
        }

        fun getNextJetDirection(): Direction {
            val retVal = jetsSequence[jetsIndex++]
            if (jetsIndex == jetsSequence.size) jetsIndex = 0
            return retVal
        }

        fun isOccupied(coord: Coord): Boolean {
            return solidifiedRocksCoords.contains(coord)
        }

        fun addSolidifiedRockElement(coord: Coord) {
            if (coord.y > topMostElement) topMostElement = coord.y
            solidifiedRocksCoords += coord
        }

        fun getHeight(): Long {
            return topMostElement + 1 + hiddenHeight
        }

//        private fun reduce() {
//            val reduceBy = topMostElement - 5
//            if (reduceBy > 0) {
//                solidifiedRocksCoords = solidifiedRocksCoords
//                    .map { it.move(Coord(-reduceBy, 0)) }
//                    .filter { it.y >= 0 }
//                    .toMutableSet()
//                hiddenHeight += reduceBy
//                topMostElement -= reduceBy
//            }
//        }

        fun render() {
            val lines = Array(topMostElement.toInt() + 1) { CharArray(CAVE_WIDTH) { ' ' } }
            solidifiedRocksCoords.forEach { lines[it.y.toInt()][it.x.toInt()] = '#' }
            println("---------")
            lines.reverse()
            lines.forEach { println("|" + String(it) + "|") }
            println("+-------+")
        }

        init {
            jetsSequence = jetsString.map { Direction.fromChar(it) }
        }
    }

    override fun solve(lines: List<String>): Long {
        val cave = Cave(lines[0])
        var knownProblems: MutableList<Set<Coord>> = mutableListOf()
        var previousKnownProblemTopMostElement = 0L
        // Basically it's about finding a reapeatable subproblem, that has same starting jet sequence index, shape and same "bottom of cave".
        // Then ignoring all the iterations that fit within the upper bound and calculating the remaining part
        // It would just require refactoring and - notably - reducing the bottom of the cave.
        // It would take too much time to solve it right now :( so I'm leaving it for later.
        for (i in 0 until iterations) {
            if (cave.jetsIndex == 0 && cave.rocksCounter % 5L == 0L) {
                println("TRAP - repeatable problem")
                previousKnownProblemTopMostElement = cave.topMostElement
                cave.render()
                knownProblems += cave.solidifiedRocksCoords
            }
            cave.spawnRock().simulate()
        }
        //cave.render()
        return cave.getHeight()
    }
}

fun main() {
    val result = Puzzle17b(Puzzle17b.ITERATIONS).solveForFile()
    println("---")
    println(result)
}