import Puzzle17b.Direction.DOWN

class Puzzle17b : Puzzle<Long> {

    companion object {
        const val CAVE_WIDTH = 7
        const val ITERATIONS = 1000000000000L
//        const val ITERATIONS = 2022L
    }

    enum class Direction(val vector: Coord) {
        LEFT(Coord(0, -1)), RIGHT(Coord(0, 1)), DOWN(Coord(-1, 0));

        companion object {
            fun fromChar(char: Char): Direction {
                return if (char == '<') LEFT else RIGHT
            }
        }
    }

    data class Coord(val y: Long, val x: Int) {
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

    data class TowerStateAtPotentialIteration(val iteration: Long, val height: Long)

    data class SurfaceShape(val relativeColumnHeights: MutableList<Long> = mutableListOf(0, 0, 0, 0, 0, 0, 0))

    class Cave(jetsString: String) {
        val jetsSequence: List<Direction>
        val storedRelativeSurfaceHeightsToTurns: MutableMap<SurfaceShape, TowerStateAtPotentialIteration> = mutableMapOf()
        private val solidifiedRocksCoords: MutableSet<Coord> = mutableSetOf()
        var height: Long = 0L
        private val surfaceShape: SurfaceShape = SurfaceShape()
        private var rocksCounter = 0
        private var jetsIndex = 0

        fun spawnRock(): Rock {
            // println("Spawning rock ${rocksCounter + 1}")
            val spawnCoords = Coord(height + 3, 2)
            val shape = Shape.nextShape(rocksCounter++)
            if (rocksCounter == Shape.values().size) rocksCounter = 0
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
            if (coord.y + 1 > surfaceShape.relativeColumnHeights[coord.x]) surfaceShape.relativeColumnHeights[coord.x] = coord.y + 1
            solidifiedRocksCoords += coord
        }

        fun getRelativeSurfaceShape(): SurfaceShape {
            return SurfaceShape(surfaceShape.relativeColumnHeights.map { it - height }.toMutableList())
        }

        fun recordRelativeSurfaceShapeAndReturnPreviousStateIfCycleMatches(iteration: Long): TowerStateAtPotentialIteration? {
            val shape = getRelativeSurfaceShape()
            if (storedRelativeSurfaceHeightsToTurns.contains(shape)) {
                return storedRelativeSurfaceHeightsToTurns[shape]!!
            }
            storedRelativeSurfaceHeightsToTurns.put(shape, TowerStateAtPotentialIteration(iteration, height))
            return null
        }

//        fun render() {
//            val lines:Array<Long> = Array(height) { CharArray(CAVE_WIDTH) { ' ' } }
//            solidifiedRocksCoords.forEach { lines[it.y][it.x] = '#' }
//            println("---------")
//            lines.reverse()
//            lines.forEach { println("|" + String(it) + "|") }
//            println("+-------+")
//        }

        init {
            jetsSequence = jetsString.map { Direction.fromChar(it) }
        }
    }

    override fun solve(lines: List<String>): Long {
        val cave = Cave(lines[0])
        var iterationToJumpTo: Long = ITERATIONS
        var heightToAdd: Long = 0
        for (i in 0 until ITERATIONS) {
            // println("Height at start of iteration ${i}: ${cave.height}")
            if (i % Shape.values().size == 0L && i % cave.jetsSequence.size == 0L) {
                println("Potential cycle ${i}")
                val towerStateAtPotentialIteration = cave.recordRelativeSurfaceShapeAndReturnPreviousStateIfCycleMatches(i)
                if (towerStateAtPotentialIteration != null) {
                    println("FULL CYCLE FOUND!!! iteration=${i}, ${towerStateAtPotentialIteration}")
                    val cycleLength = i - towerStateAtPotentialIteration.iteration
                    val fullCyclesToSkip = (ITERATIONS - i) / cycleLength
                    val iterationsToSkip = fullCyclesToSkip * cycleLength
                    iterationToJumpTo = i + iterationsToSkip
                    println("Skipping $iterationsToSkip iterations")
                    heightToAdd = (cave.height - towerStateAtPotentialIteration.height) * fullCyclesToSkip
                    break
                }
            }
            cave.spawnRock().simulate()
            // cave.render()
        }
        for (i in iterationToJumpTo until ITERATIONS) {
            // println("Running last set of iterations: Iteration ${i}")
            cave.spawnRock().simulate()
        }
        return cave.height + heightToAdd
    }
}


// 1575917611359 is too low ;[
// 1575931232076 is the right answer
fun main() {
    val result = Puzzle17b().solveForFile()
    println("---")
    println(result)
}