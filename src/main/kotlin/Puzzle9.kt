import kotlin.math.abs

class Puzzle9 : Puzzle<Int> {

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
        fun move(direction: Direction): Coords {
            return Coords(this.x + direction.dx, this.y + direction.dy)
        }
    }

    class Rope {
        companion object {
            val INITIAL_COORDS = Coords(0, 0)
        }

        private var head: Coords = INITIAL_COORDS
        private var previousHead: Coords = INITIAL_COORDS
        internal var tail: Coords = INITIAL_COORDS
        private val tailVisitedCoords: MutableSet<Coords> = mutableSetOf(tail)

        fun moveHead(direction: Direction) {
            previousHead = head
            head = head.move(direction)
            adjustTail()
        }

        fun getTailVisitedUniquePlacesCount(): Int {
            return tailVisitedCoords.size
        }

        private fun moveTail(coords: Coords) {
            tail = coords
            tailVisitedCoords.add(coords)
        }

        private fun adjustTail() {
            val diffX = head.x - tail.x
            val diffY = head.y - tail.y
            if (abs(diffX) > 1 || abs(diffY) > 1) {
                moveTail(previousHead) // Turns out I
            }
        }

    }

    override fun solve(lines: List<String>): Int {
        val rope = Rope()
        lines.map { MoveCommand.fromString(it) }.forEach { it.execute(rope) }
        return rope.getTailVisitedUniquePlacesCount()
    }

}

fun main() {
    val result = Puzzle9().solveForFile()
    println("---")
    println(result)
}
