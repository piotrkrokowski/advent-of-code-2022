class Puzzle12b : Puzzle<Int> {

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

        fun findAllLowest(): Set<Coords> {
            return map.flatMapIndexed { row: Int, fields: Array<Field> ->
                fields.mapIndexed { col: Int, field: Field -> Pair(Coords(row, col), field.height) }
            }
                .filter { it.second == 0 }
                .map { it.first }
                .toSet()
        }
    }

    data class Path(
        val currentCoords: Coords,
        val visitedNodes: List<Coords>,
        val map: Map
    ) {
        val success: Boolean = (currentCoords == map.destination)

        private fun append(coords: Coords): Path {
            return Path(coords, ArrayList(visitedNodes + coords), map)
        }

        fun score(): Int {
            if (success) return Int.MAX_VALUE
            return map.getField(currentCoords).height
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
            for (row in 0 until map.height) {
                for (col in 0 until map.width) {
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

    private fun findPath(map: Map): List<Path> {
        // println(path.visitedNodes)
        var paths: List<Path> = map.findAllLowest().map { Path(it, listOf(it), map) }
        val globalVisitedNodes: MutableSet<Coords> = mutableSetOf(map.startingPoint)
        var iteration = 0
        while (!paths.any { it.success }) {
            println("Iteration $iteration")
            paths = paths
                .flatMap { it.findPossibleSubPathsOneStep(globalVisitedNodes) }
                .sortedByDescending { it.score() }
//            if (paths.size > 10_000)
//                paths = paths.dropLast(paths.size / 2)
//                .onEach { it.draw() }
            iteration++
        }
        return paths.filter { it.success }
    }

    override fun solve(lines: List<String>): Int {
        val map = Map(lines[0].length, lines.size)
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
        return allPaths.filter { it.success }.map { it.visitedNodes.size }.sorted()[0] - 1
    }
}

fun main() {
    val result = Puzzle12b().solveForFile()
    println("---")
    println(result)
}