class Puzzle12 : Puzzle<Int> {

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
    }

    class Map(val width: Int, val height: Int) {
        var startingPoint: Coords = Coords(0, 0)
        var destination: Coords = Coords(0, 0)
        val map: Array<Array<Field>> = Array(height) { Array(width) { Field() } }

        fun draw() {
            for (row in 0 until height) {
                for (col in 0 until width) {
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
    }

    data class Path(
        val currentCoords: Coords,
        val visitedNodes: List<Coords>,
        val map: Map
    ) {
        val success: Boolean = (currentCoords == map.destination)

        fun append(coords: Coords): Path {
            return Path(coords, ArrayList(visitedNodes + coords), map)
        }

        fun score(): Int {
            if (success) return Int.MAX_VALUE
            val height = map.getField(currentCoords).height
            // val distanceScore = currentCoords.distance(map.destination) - map.startingPoint.distance(map.destination)
            return height
        }

        fun findPossibleSubPathsOneStep(globalVisitedNodes: MutableSet<Coords>): List<Path> {
            val possiblePaths: MutableList<Path> = ArrayList()
            for (direction in Direction.values()) {
                val newCoords = currentCoords.move(direction)
                if (map.areWithinBounds(newCoords) && !globalVisitedNodes.contains(newCoords)) {
                    if (map.getField(newCoords).height <= map.getField(currentCoords).height + 1) {
                        val possiblePath = append(newCoords)
                        globalVisitedNodes.add(newCoords)
                        possiblePaths.add(possiblePath)
                    }
                }
            }
            return possiblePaths
        }

        override fun toString(): String {
            return "Path(currentCoords=$currentCoords, success=$success, visitedNodes=$visitedNodes)"
        }

        @Suppress("unused")
        fun draw() {
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

    fun findPath(map: Map): List<Path> {
        // println(path.visitedNodes)

        var paths: List<Path> = listOf(Path(map.startingPoint, listOf(map.startingPoint), map))
        val globalVisitedNodes: MutableSet<Coords> = mutableSetOf(map.startingPoint)
        var iteration = 0
        while (!paths.any { it.success }) {
            println("Iteration ${iteration}")
            paths = paths
                .flatMap { it.findPossibleSubPathsOneStep(globalVisitedNodes) }
                .sortedByDescending { it.score() }
            paths.get(0).currentCoords
            if (paths.size > 10_000)
                paths = paths.dropLast(paths.size / 2)
//                .onEach { it.draw() }
            iteration++
        }
        return paths.filter { it.success }
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

        val allPaths = findPath(map)
        return allPaths.filter { it.success }.map { it.visitedNodes.size }.sorted().get(0) - 1
    }
}

fun main() {
    val result = Puzzle12().solveForFile()
    println("---")
    println(result)
}