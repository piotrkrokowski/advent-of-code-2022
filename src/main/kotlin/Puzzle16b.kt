import Graph.Node
import Graph.OrderedPair

// This became a mess :)
class Puzzle16b : Puzzle<Int> {

    class Volcano : Graph<Valve>() {

        var bestScore = 0

        companion object {
            fun fromLines(lines: List<String>): Volcano {
                val volcano = Volcano()
//                lines.map { Valve.fromString(it, volcano) }.groupingBy { it.code }
//                    .reduce { _, _, element -> element }
                lines.map { Valve.fromString(it) }.forEach { volcano.addNode(it) }
                volcano.nodes.values.forEach { valve ->
                    valve.definedImmediateConnections.forEach { rawConnection ->
                        volcano.addConnection(valve.identifier, rawConnection)
                    }
                }
                return volcano
            }
        }
    }

    class Valve(
        code: String,
        val flow: Int,
        val definedImmediateConnections: List<String>
    ) : Node<Valve>(code) {

        var open: Boolean = false

        companion object {
            private val REGEX = Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)")

            fun fromString(string: String): Valve {
                // println(string)
                val found = REGEX.find(string)
                with(found!!) {
                    val connections: List<String> = groupValues[3].split(",").map { it.trim() }
                    return Valve(groupValues[1], groupValues[2].toInt(), connections)
                }
            }
        }


    }

    data class Simulation(
        var playerRemainingTime: Int,
        var elephantRemainingTime: Int,
        var pressureReleased: Int,
        var openValves: Set<String> = mutableSetOf(),
        val currentValve: Pair<Valve, Valve>,
        val volcano: Volcano,
        val path: List<Pair<String, String>>
    ) {
        var choicesSequence: List<Pair<String, String>> = mutableListOf()

        fun start(): Simulation {
            // println("Simulation for remainingtime ${playerRemainingTime}, ${elephantRemainingTime}")
            val possiblePlayerMoves = getAllPossibleMoves(currentValve.first, playerRemainingTime)
            val possibleElephantMoves = getAllPossibleMoves(currentValve.second, elephantRemainingTime)

            val possibleCombinations: List<PossibleMovesCombination> =
                possiblePlayerMoves.flatMap { playerMove ->
                    possibleElephantMoves
                        .filter { elephantMove -> elephantMove.valve != playerMove.valve }
                        .map { elephantMove ->
                            PossibleMovesCombination(playerMove, elephantMove)
                        }
                }


            val deduplicatedCombinations: List<PossibleMovesCombination> =
                possibleCombinations.groupingBy { OrderedPair(it.playerMove.valve, it.elephantMove.valve) }
                    .reduce { _, accumulator: PossibleMovesCombination, element: PossibleMovesCombination ->
                        if (accumulator.playerMove.distance + accumulator.elephantMove.distance < element.playerMove.distance + element.elephantMove.distance)
                            accumulator
                        else
                            element
                    }
                    .values.toList()

            if (deduplicatedCombinations.isEmpty()) return this


            val possibleSimulations = deduplicatedCombinations
                .map { it.runSimulation(this) }
                .sortedByDescending { it.pressureReleased }

            // println("Simulation on node ${currentValve}")
            // println(possibleSimulations.joinToString("\n"))
            val bestSimulation = possibleSimulations[0]
            // println("Choosing $bestSimulation")

            this.merge(bestSimulation)

            if (volcano.bestScore < pressureReleased) {
                volcano.bestScore = pressureReleased
                println("New best score: $pressureReleased")
            }

            // println("$path + $choicesSequence --> $openValves, score: $pressureReleased")

            return this
        }

        private fun getAllPossibleMoves(from: Valve, remainingTime: Int): List<PossibleMove> {
            return volcano.getAllPathsFrom(from.identifier)
                .asSequence()
                .filter { it.nodePair.to.flow > 0 }
                .filter { it.nodePair.to != from }
                .filter { it.nodePair.to.identifier !in openValves }
                .map { path -> PossibleMove(path.nodePair.to, path.distance) }
                .filter { it.getTimeCost() <= remainingTime }
                .toList()
        }

        private fun merge(bestSimulation: Simulation) {
            choicesSequence =
                (listOf(bestSimulation.currentValve).map { it.first.identifier to it.second.identifier }) + bestSimulation.choicesSequence
            playerRemainingTime = bestSimulation.playerRemainingTime
            openValves = bestSimulation.openValves
            pressureReleased = bestSimulation.pressureReleased
        }

        override fun toString(): String {
            return "Simulation(remainingTime=$playerRemainingTime, pressureReleased=$pressureReleased, openValves=$openValves, currentValve=$currentValve, choicesSequence=$choicesSequence)"
        }
    }

    data class PossibleMovesCombination(val playerMove: PossibleMove, val elephantMove: PossibleMove) {
        fun runSimulation(parentSimulation: Simulation): Simulation {
            val simulation = Simulation(
                parentSimulation.playerRemainingTime - playerMove.getTimeCost(),
                parentSimulation.elephantRemainingTime - elephantMove.getTimeCost(),
                parentSimulation.pressureReleased + playerMove.getOwnScore(parentSimulation.playerRemainingTime) + elephantMove.getOwnScore(
                    parentSimulation.elephantRemainingTime
                ),
                parentSimulation.openValves + playerMove.valve.identifier + elephantMove.valve.identifier,
                Pair(playerMove.valve, elephantMove.valve),
                parentSimulation.volcano,
                parentSimulation.path + Pair(playerMove.valve.identifier, elephantMove.valve.identifier)
            )
            simulation.start()
            return simulation
        }
    }

    data class PossibleMove(val valve: Valve, val distance: Int) {
        fun getOwnScore(remainingTime: Int): Int {
            return (remainingTime - getTimeCost()) * valve.flow
        }

        fun getTimeCost(): Int {
            return distance + 1
        }

        override fun toString(): String {
            return "PossibleNodeChoice(valve=$valve, distance=$distance})"
        }
    }

    override fun solve(lines: List<String>): Int {
        val volcano = Volcano.fromLines(lines)
        volcano.calculateDistances()
        val startingPoint = volcano["AA"]!!
        val simulation = Simulation(
            30 - 4, 30 - 4, 0, mutableSetOf(), startingPoint to startingPoint, volcano, listOf(Pair("AA", "AA"))
        ).start()
        return simulation.pressureReleased
    }

}

fun main() {
    val result = Puzzle16b().solveForFile()
    println("---")
    println(result)
}
