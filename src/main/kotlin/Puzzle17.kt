import Puzzle17.Direction.DOWN

class Puzzle17 : Puzzle<Int> {

    companion object {
        const val CAVE_WIDTH = 7
    }

    enum class Direction(val vector: Coord) {
        LEFT(Coord(0, -1)), RIGHT(Coord(0, 1)), DOWN(Coord(-1, 0));

        companion object {
            fun fromChar(char: Char): Direction {
                return if (char == '<') LEFT else RIGHT
            }
        }
    }

    data class Coord(val y: Int, val x: Int) {
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
            fun nextShape(index: Int): Shape {
                return values()[index % values().size]
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
            if (doesCollideAfterMove(DOWN)) {
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
        private val jetsSequence: List<Direction>
        private val solidifiedRocksCoords: MutableSet<Coord> = mutableSetOf()
        private var height = 0
        private var rocksCounter = 0
        private var jetsIndex = 0

        fun spawnRock(): Rock {
            println("Spawning rock ${rocksCounter + 1}")
            val spawnCoords = Coord(height + 3, 2)
            val shape = Shape.nextShape(rocksCounter++)
            return Rock(shape, spawnCoords, this)
        }

        fun getNextJetDirection(): Direction {
            return jetsSequence[(jetsIndex++) % jetsSequence.size]
        }

        fun isOccupied(coord: Coord): Boolean {
            return solidifiedRocksCoords.contains(coord)
        }

        fun addSolidifiedRockElement(coord: Coord) {
            if (coord.y + 1 > height) height = coord.y + 1
            solidifiedRocksCoords += coord
        }

        fun getHeight(): Int {
            return height
        }

        fun render() {
            val lines = Array(height) { CharArray(CAVE_WIDTH) { ' ' } }
            solidifiedRocksCoords.forEach { lines[it.y][it.x] = '#' }
            println("---------")
            lines.reverse()
            lines.forEach { println("|" + String(it) + "|") }
            println("+-------+")
        }

        init {
            jetsSequence = jetsString.map { Direction.fromChar(it) }
        }
    }

    override fun solve(lines: List<String>): Int {
        val cave = Cave(lines[0])
        repeat(2022) {
            cave.spawnRock().simulate()
            // cave.render()
        }
        return cave.getHeight()
    }
}

fun main() {
    val result = Puzzle17().solveForFile()
    println("---")
    println(result)
}