import kotlin.math.abs

class Puzzle12TooSlow : Puzzle<Int> {

    class Field {
        var height: Int = 0
    }

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);
    }

    data class Coords(val row: Int, val col: Int) {
        fun move(direction: Direction): Coords {
            return Coords(this.row + direction.dy, this.col + direction.dx)
        }

        override fun toString(): String {
            return "(${row}, ${col})"
        }

        fun distance(destination: Coords): Int {
            return abs(destination.col - col) + abs(destination.row - row)
        }
    }

    class Map(val width: Int, val height: Int) {
        var startingPoint: Coords = Coords(0, 0)
        var destination: Coords = Coords(0, 0)
        val map: Array<Array<Field>> = Array(height) { Array(width) { Field() } }

        fun draw() {
            for (row in 0..height - 1) {
                for (col in 0..width - 1) {
                    val h = map[row][col]
                    print(Char('a'.code + h.height))
                }
                println()
            }
        }

        fun areWithinBounds(coords: Coords): Boolean {
            return coords.col in 0 until width && coords.row in 0 until height
        }

        fun getField(currentCoords: Coords): Field {
            return map[currentCoords.row][currentCoords.col]
        }

        fun heightDiff(candidateCoords: Coords, previousCoords: Coords): Int {
            return getField(candidateCoords).height - getField(previousCoords).height
        }

        fun distanceDiff(candidateCoords: Coords, previousCoords: Coords): Int {
            return candidateCoords.distance(destination) - previousCoords.distance(destination)
        }

        fun moveScore(candidateCoords: Coords, previousCoords: Coords): Int {
            if (!areWithinBounds(candidateCoords)) return Int.MIN_VALUE // FIXME
            val heightDiff = heightDiff(candidateCoords, previousCoords)
            if (heightDiff > 1) return Int.MIN_VALUE // FIXME
            return heightDiff * 2 - distanceDiff(candidateCoords, previousCoords)
        }


    }

    data class Path(
        val currentCoords: Coords,
        val visitedNodes: List<Coords>,
        val success: Boolean = false
    ) {
        fun append(coords: Coords): Path {
            return Path(coords, ArrayList(visitedNodes + coords), false)
        }

        fun success(): Path {
            return Path(currentCoords, visitedNodes, true)
        }

        override fun toString(): String {
            return "Path(currentCoords=$currentCoords, success=$success, visitedNodes=$visitedNodes)"
        }

        fun draw(map: Map) {
            println("-----------")
            for (row in 0..map.height - 1) {
                for (col in 0..map.width - 1) {
                    val coords = Coords(row, col)
                    if (coords == currentCoords) {
                        print('@')
                    } else if (coords in visitedNodes) {
                        print('*')
                    } else if (coords == map.startingPoint) {
                        print('S')
                    } else if (coords == map.destination) {
                        print('E')
                    } else {
                        val h = map.map[row][col]
                        print(Char('a'.code + h.height))
                    }
                }
                println()
            }
            println("-----------")
        }
    }

    fun findPath(path: Path, map: Map): List<Path> {
        // println(path.visitedNodes)
        path.draw(map)
        if (map.destination.equals(path.currentCoords)) {
            println("Destination reached: ${path}")
            return listOf(path.success())
        }
        val possiblePaths: MutableList<Path> = ArrayList()
        val directionsSorted: List<Direction> = Direction
            .values()
            .map { Pair(it, map.moveScore(path.currentCoords.move(it), path.currentCoords)) }
            .sortedByDescending { it.second }
            //.onEach { println(it) }
            .map { it.first }
        for (direction in directionsSorted) {
            val newCoords = path.currentCoords.move(direction)
            if (map.areWithinBounds(newCoords) && !path.visitedNodes.contains(newCoords)) {
                if (map.getField(newCoords).height <= map.getField(path.currentCoords).height + 1) {
                    val possiblePath = path.append(newCoords)
//                    println("Evaluating path ${possiblePath.visitedNodes}")
                    val found = findPath(possiblePath, map)
                    possiblePaths.addAll(found)
                }
            }
        }
//        println("Returning possible paths: ${possiblePaths}")
        return possiblePaths.filter { it.success }
    }

    override fun solve(lines: List<String>): Int {
        val map = Map(lines.get(0).length, lines.size)
        for (row in lines.indices) {
            val line = lines[row]
            for (col in line.indices) {
                val c = line[col]
                var height: Int = c.code - 'a'.code
                if (c == 'S') {
                    height = 0
                    map.startingPoint = Coords(row, col)
                }
                if (c == 'E') {
                    height = 'z'.code - 'a'.code
                    map.destination = Coords(row, col)
                }
                map.map[row][col].height = height
            }
        }
        map.draw()

        val allPaths = findPath(Path(map.startingPoint, listOf(map.startingPoint)), map)
        return allPaths.filter { it.success }.map { it.visitedNodes.size }.sorted().get(0) - 1
    }
}

fun main() {
    val result = Puzzle12TooSlow().solveForFile { s -> "src/main/resources/puzzle12.txt" }
    println("---")
    println(result)
}