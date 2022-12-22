import Puzzle22.Orientation.*

class Puzzle22 : Puzzle<Int> {

    enum class Orientation(val value: Int, val function: (Coords) -> Coords, val c: Char) {
        RIGHT(0, { Coords(it.row, it.col + 1) }, '>'),
        DOWN(1, { Coords(it.row + 1, it.col) }, 'v'),
        LEFT(2, { Coords(it.row, it.col - 1) }, '<'),
        UP(3, { Coords(it.row - 1, it.col) }, '^')
    }

    data class Coords(val row: Int, val col: Int)

    data class Range(val start: Int, val end: Int) {
        fun isInRange(index: Int): Boolean {
            return index in start..end
        }
    }

    class Maze(
        // Everything is zero-indexed
        val walls: Set<Coords>,
        val rowsEdges: Array<Range>,
        val colsEdges: Array<Range>,
        val height: Int,
        val width: Int
    ) {
        val visited: MutableMap<Coords, Char> = mutableMapOf()
        var player: Player = Player(getStartingCoords(), RIGHT, this)

        fun isInRange(coords: Coords): Boolean {
            return isInRange(coords.row, coords.col)
        }

        private fun isWall(row: Int, col: Int): Boolean {
            return walls.contains(Coords(row, col))
        }

        private fun isInRange(row: Int, col: Int): Boolean {
            if (row < 0 || col < 0 || row == height || col == width) return false
            return rowsEdges[row].isInRange(col) && colsEdges[col].isInRange(row)
        }

        fun getStartingCoords(): Coords {
            return Coords(0, rowsEdges[0].start)
        }

        override fun toString(): String {
            val sb = StringBuilder()
            for (row in 0 until height) {
                for (col in 0 until width) {
                    val coords = Coords(row, col)
                    if (player.coords == coords) sb.append('@')
                    else if (visited.contains(coords)) sb.append(visited[coords]!!)
                    else if (isWall(row, col)) sb.append('#')
                    else if (isInRange(row, col)) sb.append('.')
                    else sb.append(' ')
                }
                sb.appendLine()
            }
            sb.appendLine()
            return sb.toString()
        }

        companion object {
            fun build(lines: List<String>): Maze {
                val height = lines.size
                val width = lines.map { it.length }.max()

                val walls: MutableSet<Coords> = mutableSetOf()
                val rowsEdges: Array<Range> = Array(height) { Range(-1, -1) }
                val colsEdges: Array<Range> = Array(width) { Range(-1, -1) }
                for (row in lines.indices) {
                    var line = lines[row]
                    line = line.padEnd(width, ' ')
                    val rowStart = line.indexOfFirst { it != ' ' }
                    val rowEnd = line.indexOfLast { it != ' ' }
                    rowsEdges[row] = Range(rowStart, rowEnd)
                    setColsEdgesBoringStuff(line, row, width, colsEdges)
                    addWalls(line, walls, row, rowStart, rowEnd)
                }
                setColsEdgesBoringStuff(" ".repeat(width), height, width, colsEdges)
                return Maze(walls, rowsEdges, colsEdges, height, width)
            }

            private fun addWalls(line: String, walls: MutableSet<Coords>, row: Int, rowStart: Int, rowEnd: Int) {
                for (col in rowStart..rowEnd) {
                    if (line[col] == '#') walls.add(Coords(row, col))
                }
            }

            private fun setColsEdgesBoringStuff(
                line: String, row: Int, width: Int, colsEdges: Array<Range>
            ) {
                for (col in 0 until width) {
                    if (colsEdges[col].start == -1 && line[col] != ' ') {
                        colsEdges[col] = Range(row, -1)
                    } else if (colsEdges[col].start != -1 && colsEdges[col].end == -1 && line[col] == ' ') {
                        // This is dirty... but should work
                        colsEdges[col] = Range(colsEdges[col].start, row - 1)
                    }
                }
            }
        }
    }

    interface Order {
        fun apply(player: Player)
    }

    data class MoveOrder(val steps: Int) : Order {
        override fun apply(player: Player) {
            player.move(steps)
        }
    }

    data class TurnOrder(val rightTurns: Int) : Order {
        override fun apply(player: Player) {
            player.turn(rightTurns)
        }
    }

    class Orders(val orders: List<Order>) {

        fun applyToPlayer(player: Player) {
            orders.forEach { it.apply(player) }
        }

        companion object {
            private val REGEX: Regex = Regex("(\\d+|[LR])")
            fun fromString(string: String): Orders {
                val orders = mutableListOf<Order>()
                val allFound = REGEX.findAll(string)
                for (found in allFound) {
                    if (found.value in listOf("L", "R")) {
                        orders.add(TurnOrder(if (found.value == "R") 1 else -1))
                    } else {
                        orders.add(MoveOrder(found.value.toInt()))
                    }
                }
                return Orders(orders)
            }
        }
    }

    class Player(var coords: Coords, var orientation: Orientation, private val maze: Maze) {
        fun turn(rightTurns: Int) {
            val values = values()
            var newOrientationIndex = (orientation.value + rightTurns) % values.size
            if (newOrientationIndex < 0) {
                newOrientationIndex += values.size
            }
            orientation = values[newOrientationIndex]
            println("Turning $rightTurns. New orientation is $orientation")
        }

        fun move(steps: Int) {
            println("Stepping $steps forward")
            for (step in 0 until steps) {
                var newCoords = orientation.function.invoke(coords)
                if (!maze.isInRange(newCoords)) newCoords = wrapCoords()

                if (maze.walls.contains(newCoords)) {
                    println("Hit wall")
                    break
                }

                markVisitedSpotForDebugging(newCoords)
                coords = newCoords
                println("New coords: $newCoords")
            }
            // println(maze.toString())
            // println("------")
        }

        private fun wrapCoords(): Coords {
            return if (orientation == RIGHT) {
                Coords(coords.row, maze.rowsEdges[coords.row].start)
            } else if (orientation == LEFT) {
                Coords(coords.row, maze.rowsEdges[coords.row].end)
            } else if (orientation == DOWN) {
                Coords(maze.colsEdges[coords.col].start, coords.col)
            } else {
                Coords(maze.colsEdges[coords.col].end, coords.col)
            }
        }

        private fun markVisitedSpotForDebugging(newCoords: Coords) {
            if (maze.visited.containsKey(newCoords))
                maze.visited[newCoords] = '*'
            else
                maze.visited[newCoords] = orientation.c
        }
    }

    override fun solve(lines: List<String>): Int {
        val maze = Maze.build(lines.dropLast(2))
        println(maze.toString())
        val orders = Orders.fromString(lines.last())
        orders.applyToPlayer(maze.player)
        println(maze.toString())
        println(maze.player.coords)
        return calculateResult(maze.player)
    }

    private fun calculateResult(player: Player): Int {
        return ((player.coords.row + 1) * 1000) + ((player.coords.col + 1) * 4) + player.orientation.value
    }
}

fun main() {
    val result = Puzzle22().solveForFile()
    println("---")
    println(result)
}