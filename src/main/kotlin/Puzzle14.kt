import Puzzle14.Direction.*
import Puzzle14.Field.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class Puzzle14 : Puzzle<Int> {

    enum class Field(val c: Char) {
        ROCK('#'), AIR('.'), SAND('o');

        override fun toString(): String {
            return c.toString()
        }
    }

    data class Coords(val x: Int, val y: Int) {
        fun transpose(direction: Direction): Coords {
            return Coords(x + direction.dx, y + direction.dy)
        }

        companion object {
            fun fromString(string: String): Coords {
                val split = string.split(",")
                return Coords(split[0].trim().toInt(), split[1].trim().toInt())
            }
        }
    }

    enum class Direction(val dx: Int, val dy: Int) {
        RIGHT(1, 0), DOWN(0, 1), DOWN_LEFT(-1, 1), DOWN_RIGHT(1, 1)
    }

    class SandBlock(var coords: Coords, val cave: Cave) {
        fun isInAbyss(): Boolean {
            return !cave.isWithinBounds(coords)
        }

        fun drop(): SandBlock {
            while (nextMove() && !isInAbyss()) {
                // println(coords)
            }
            if (!isInAbyss()) {
                cave.putAt(coords, SAND)
            }
            return this
        }

        private fun nextMove(): Boolean {
            val move: Direction? = listOf(DOWN, DOWN_LEFT, DOWN_RIGHT).find { isMovePossible(it) }
            if (move != null) {
                coords = coords.transpose(move)
                return true
            } else {
                return false
            }
        }

        private fun isMovePossible(direction: Direction): Boolean {
            val newCoords = coords.transpose(direction)
            if (!cave.isWithinBounds(newCoords)) return true
            return cave.getAt(newCoords) == AIR
        }
    }


    class Cave(val width: Int = 1000, val height: Int = 500, val sandSource: Coords = Coords(500, 0)) {
        private var map: Array<Array<Field>> = Array(width) { Array(height) { AIR } }
        var minX: Int = sandSource.x
        var minY: Int = sandSource.y
        var maxX: Int = sandSource.x
        var maxY: Int = sandSource.y

        fun getAt(coords: Coords): Field {
            return map[coords.x][coords.y]
        }

        fun addRocksFromString(line: String) {
            val split = line.split("->")
            for (i in 0..split.size - 2) {
                addRocks(Coords.fromString(split[i]), Coords.fromString(split[i + 1]))
            }
        }

        fun spawnSandBlock(): SandBlock {
            return SandBlock(sandSource, this)
        }

        fun isWithinBounds(coords: Coords): Boolean {
            return coords.x in 0 until width && coords.y in 0 until height
        }

        fun addRocks(from: Coords, to: Coords) {
            if (from.x != to.x && from.y != to.y) throw UnsupportedOperationException("Don't know how to draw diagonal line")
            if (from.x != to.x) {
                drawLine(Coords(Math.min(from.x, to.x), from.y), abs(from.x - to.x) + 1, RIGHT)
            } else {
                drawLine(Coords(from.x, Math.min(from.y, to.y)), abs(from.y - to.y) + 1, DOWN)
            }
        }

        fun putAt(coords: Coords, field: Field) {
            map[coords.x][coords.y] = field
            if (field == ROCK) {
                minX = min(minX, coords.x)
                maxX = max(maxX, coords.x)
                minY = min(minY, coords.y)
                maxY = max(maxY, coords.y)
            }
        }

        private fun drawLine(start: Coords, length: Int, direction: Direction) {
            var coords = start
            repeat(length) {
                putAt(coords, ROCK)
                coords = coords.transpose(direction)
            }
        }

        fun draw(): String {
            return draw(minX, minY, maxX, maxY)
        }

        fun draw(minX: Int, minY: Int, maxX: Int = width, maxY: Int = height): String {
            val sb: StringBuilder = StringBuilder()
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    if (Coords(x, y) == sandSource)
                        sb.append('+')
                    else
                        sb.append(map[x][y].toString())
                }
                sb.append("\n")
            }
            return sb.toString()
        }

        companion object {
            fun fromLines(lines: List<String>): Cave {
                val cave = Cave()
                lines.forEach { cave.addRocksFromString(it) }
                return cave
            }
        }
    }

    override fun solve(lines: List<String>): Int {
        val cave = Cave.fromLines(lines)
        var result = 0
        while (!cave.spawnSandBlock().drop().isInAbyss()) {
            result++
            println(cave.draw())
        }
        return result
    }
}

fun main() {
    val result = Puzzle14().solveForFile()
    println("---")
    println(result)
}