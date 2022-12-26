@file:Suppress("MemberVisibilityCanBePrivate")

import Puzzle24.Direction.*

class Puzzle24 : Puzzle<Int> {

    enum class Direction(val dx: Int, val dy: Int, val char: Char) {
        STAY(0, 0, ' '),
        UP(0, -1, '^'),
        RIGHT(1, 0, '>'),
        DOWN(0, 1, 'v'),
        LEFT(-1, 0, '<');
    }

    data class Coords(val x: Int, val y: Int) {
        fun move(direction: Direction): Coords {
            return Coords(x + direction.dx, y + direction.dy)
        }

        override fun toString(): String {
            return "[$x, $y]"
        }
    }

    data class Blizzard(val coords: Coords, val direction: Direction) {
        fun move(valley: Valley): Blizzard {
            var newCoords = coords.move(direction)
            if (!withinBounds(newCoords, valley)) newCoords = wrap(newCoords, valley)
            return Blizzard(newCoords, direction)
        }

        private fun withinBounds(newCoords: Coords, valley: Valley): Boolean {
            return newCoords.x in 1..valley.width - 2 && newCoords.y in 1..valley.height - 2
        }

        private fun wrap(coordsToWrap: Coords, valley: Valley): Coords {
            return when (direction) {
                UP -> Coords(coordsToWrap.x, valley.height - 2)
                DOWN -> Coords(coordsToWrap.x, 1)
                RIGHT -> Coords(1, coordsToWrap.y)
                LEFT -> Coords(valley.width - 2, coordsToWrap.y)
                else -> {
                    throw IllegalStateException()
                }
            }
        }
    }

    class ValleyInTurn(
        val turn: Int,
        val blizzards: List<Blizzard>,
        val valley: Valley
    ) {
        val blizzardsCoords: Set<Coords> = blizzards.map { it.coords }.toSet() // Redundant for performance

        fun nextTurn(): ValleyInTurn {
            return ValleyInTurn(turn + 1, blizzards.map { it.move(valley) }, valley)
        }

        override fun toString(): String {
            val sb = StringBuilder()
            for (y in 0 until valley.height) {
                for (x in 0 until valley.width) {
                    val coords = Coords(x, y)
                    if (valley.walls.contains(coords)) sb.append('#')
                    else {
                        val blizzardsInPosition = blizzards.filter { it.coords == coords }
                        if (blizzardsInPosition.size > 1)
                            sb.append(blizzardsInPosition.size)
                        else if (blizzardsInPosition.size == 1)
                            sb.append(blizzardsInPosition[0].direction.char)
                        else
                            sb.append('.')
                    }
                }
                sb.appendLine()
            }
            return sb.toString()
        }
    }

    class Valley(
        // Holds immutable data for valley and mutable cached states for each minute
        val width: Int,
        val height: Int,
        val walls: Set<Coords>,
        blizzards: List<Blizzard>,
    ) {
        val startingPoint: Coords = Coords(1, 0)
        val exitPoint: Coords = Coords(width - 2, height - 1)
        private val valleyInTurn: MutableList<ValleyInTurn> = mutableListOf(ValleyInTurn(0, blizzards, this))

        fun getValleyInTurn(turn: Int): ValleyInTurn {
            if (turn < 0) throw IllegalArgumentException()
            if (turn !in valleyInTurn.indices) {
                if ((turn - 1) !in valleyInTurn.indices) getValleyInTurn(turn - 1)
                val valleyInPreviousTurn = valleyInTurn[turn - 1]
                valleyInTurn.add(turn, valleyInPreviousTurn.nextTurn())
            }
            return valleyInTurn[turn]
        }

        fun isWithinBounds(coords: Coords): Boolean {
            return coords.x in 0 until width && coords.y in 0 until height
        }

        companion object {
            fun fromLines(lines: List<String>): Valley {
                val walls: MutableSet<Coords> = mutableSetOf()
                val blizzards: MutableList<Blizzard> = mutableListOf()
                for (y in lines.indices) {
                    val line = lines[y]
                    for (x in line.indices) {
                        val coords = Coords(x, y)
                        when (line[x]) {
                            '#' -> walls.add(coords)
                            '<' -> blizzards.add(Blizzard(coords, LEFT))
                            '>' -> blizzards.add(Blizzard(coords, RIGHT))
                            '^' -> blizzards.add(Blizzard(coords, UP))
                            'v' -> blizzards.add(Blizzard(coords, DOWN))
                        }
                    }
                }
                return Valley(lines[0].length, lines.size, walls, blizzards)
            }
        }
    }

    class PlayerSimulation(val round: Int, val coords: Coords, val valley: Valley, val movesHistory: List<Coords>, val solver: Puzzle24) {

        fun calculateOptions(): List<PlayerSimulation> {
            if (isWon()) throw IllegalStateException("Should never been called")
            if (solver.bestWinningSimulation != null && solver.bestWinningSimulation!!.round < round) {
                // println("Giving up")
                return emptyList()
            }
            val possibleMoves = possibleMoves()
            return possibleMoves.map { createSimulation(it) }
        }

        private fun createSimulation(newCoords: Coords): PlayerSimulation {
            return PlayerSimulation(
                round + 1,
                newCoords,
                valley,
                movesHistory + newCoords,
                solver
            )
        }

        fun isWon(): Boolean {
            return coords == valley.exitPoint
        }

        fun possibleMoves(): List<Coords> {
            val valleyNextTurn = valley.getValleyInTurn(round + 1)
            return Direction.values()
                .map { coords.move(it) }
                .filter { valley.isWithinBounds(it) }
                .filter { !valley.walls.contains(it) }
                .filter { !valleyNextTurn.blizzardsCoords.contains(it) }
        }

        fun distanceToExit(): Int {
            return (valley.width - coords.x) + (valley.height - coords.y) - 2
        }

        override fun toString(): String {
            return "PlayerSimulation(round=$round, round=${round}, distance=${distanceToExit()}"
        }

        fun getCheckedCombination(): CheckedCombination {
            return CheckedCombination(round, coords)
        }
    }

    data class CheckedCombination(val round: Int, val coords: Coords)

    var bestWinningSimulation: PlayerSimulation? = null
    val simulations: MutableList<PlayerSimulation> = mutableListOf()
    val alreadyCheckedCombinations: MutableSet<CheckedCombination> = mutableSetOf()

    var counter: Int = 0

    override fun solve(lines: List<String>): Int {
        val valley = Valley.fromLines(lines)
        simulations.add(PlayerSimulation(0, valley.startingPoint, valley, emptyList(), this))

        while (simulations.size > 0) {
            val simulation = simulations.removeFirst()
            val checkedCombination = simulation.getCheckedCombination()
            if (!alreadyCheckedCombinations.contains(checkedCombination)) {
                alreadyCheckedCombinations.add(checkedCombination)
                val options = simulation.calculateOptions()
                val (winning, remaining) = options.partition { it.isWon() }
                winning.forEach { recordIfBest(it) }
                simulations.addAll(remaining)
            } else {
                // println("Skipping checking already checked combination $checkedCombination")
            }
            counter++
        }

        return bestWinningSimulation?.round ?: throw IllegalStateException("No solution found")
    }

    private fun recordIfBest(winningSimulation: PlayerSimulation) {
        if (bestWinningSimulation == null || bestWinningSimulation!!.round > winningSimulation.round) {
            println("New best score! $winningSimulation")
            bestWinningSimulation = winningSimulation
        }
    }
}

fun main() {
    val result = Puzzle24().solveForFile()
    println("---")
    println(result)
}