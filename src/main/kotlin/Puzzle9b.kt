import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val NUMBER_OF_ROPE_NODES = 10

class Puzzle9b : Puzzle<Int> {

    data class MoveCommand(val direction: Direction, val distance: Int) {
        fun execute(rope: Rope) {
            repeat(distance) {
                rope.moveHead(direction)
            }
        }

        companion object {
            fun fromString(line: String): MoveCommand {
                val distance = line.substring(2).toInt()
                val direction = Direction.fromChar(line[0])
                return MoveCommand(direction, distance)
            }
        }
    }

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, 1), RIGHT(1, 0), DOWN(0, -1), LEFT(-1, 0);

        companion object {
            fun fromChar(char: Char): Direction {
                return values().find { it.name[0] == char }!!
            }
        }
    }

    data class Coords(val x: Int, val y: Int) {
        fun transpose(direction: Direction): Coords {
            return Coords(this.x + direction.dx, this.y + direction.dy)
        }

        companion object {
            val INITIAL_COORDS: Coords = Coords(0, 0)
        }
    }

    class RopeNode(private val rope: Rope, private val index: Int) {
        var coords: Coords = Coords.INITIAL_COORDS
        private var previousCoords = coords

        fun move(direction: Direction) {
            if (nodeTowardsHead() != null) throw UnsupportedOperationException("Only head can be moved in direction")
            moveTo(coords.transpose(direction))
        }

        private fun moveTo(coords: Coords) {
            previousCoords = this.coords
            this.coords = coords
            nodeTowardsTail()?.adjust()
        }

        private fun adjust() {
            val previousNodeCoords = nodeTowardsHead()!!.coords
            val previousNodeDiffX = previousNodeCoords.x - coords.x
            val previousNodeDiffY = previousNodeCoords.y - coords.y
            if (abs(previousNodeDiffX) > 1 || abs(previousNodeDiffY) > 1) {
                val newCoords = Coords(
                    coords.x + notMoreThenOne(previousNodeDiffX),
                    coords.y + notMoreThenOne(previousNodeDiffY)
                )
                moveTo(newCoords)
            }
        }

        private fun notMoreThenOne(v: Int): Int {
            //return if (v < -1) -1 else if (v > 1) 1 else v
            return max(min(v, 1), -1)
        }

        private fun nodeTowardsHead(): RopeNode? {
            return if (index <= 0) null else rope.nodes[index - 1]
        }

        private fun nodeTowardsTail(): RopeNode? {
            return if (index >= rope.nodes.size - 1) null else rope.nodes[index + 1]
        }

    }

    class Rope {
        internal val nodes: Array<RopeNode> = Array(NUMBER_OF_ROPE_NODES) { RopeNode(this, it) }
        private val tailVisitedCoords: MutableSet<Coords> = mutableSetOf(Coords.INITIAL_COORDS)

        fun moveHead(direction: Direction) {
            nodes[0].move(direction)
            tailVisitedCoords.add(nodes[9].coords)
        }

        fun getTailVisitedUniquePlacesCount(): Int {
            return tailVisitedCoords.size
        }
    }

    override fun solve(lines: List<String>): Int {
        val rope = Rope()
        lines.map { MoveCommand.fromString(it) }.forEach { it.execute(rope) }
        return rope.getTailVisitedUniquePlacesCount()
    }

}

fun main() {
    val result = Puzzle9b().solveForFile()
    println("---")
    println(result)
}
