import Graph.Node
import Graph.OrderedPair
import java.util.*

// New best score: 2615, path: [(AA, AA), (LR, OV), (DK, FJ), (ST, EL), (PF, KQ), (MD, JQ), (null, IN)]
// I don't know what to think of it. I've failed, but I've succeeded ;]
class Puzzle16b : Puzzle<Int> {

    class Volcano : Graph<Valve>() {

        var bestScore = 0 // TODO: It doesn't belong here!
        val simulations: TreeSet<Simulation> = TreeSet() // TODO: It doesn't belong here!

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
            val NULL_VALVE = Valve("NULL", 0, emptyList())

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
    ) : Comparable<Simulation> {
        fun start(): Simulation {
            // println("Simulation for remainingtime ${playerRemainingTime}, ${elephantRemainingTime}")
            val possiblePlayerMoves = getAllPossibleMoves(currentValve.first, playerRemainingTime)
            val possibleElephantMoves = getAllPossibleMoves(currentValve.second, elephantRemainingTime)


            val deduplicatedCombinations: List<PossibleMovesCombination> =
                if (possiblePlayerMoves.isEmpty() && possibleElephantMoves.isNotEmpty()) {
                    possibleElephantMoves.map { PossibleMovesCombination(null, it) }
                } else if (possiblePlayerMoves.isNotEmpty() && possibleElephantMoves.isEmpty()) {
                    possiblePlayerMoves.map { PossibleMovesCombination(it, null) }
                } else {
                    val possibleCombinations = possiblePlayerMoves.flatMap { playerMove ->
                        possibleElephantMoves
                            .filter { elephantMove -> elephantMove.valve != playerMove.valve }
                            .map { elephantMove ->
                                PossibleMovesCombination(playerMove, elephantMove)
                            }
                    }

                    possibleCombinations.groupingBy { OrderedPair(it.playerMove!!.valve, it.elephantMove!!.valve) }
                        .reduce { _, accumulator: PossibleMovesCombination, element: PossibleMovesCombination ->
                            if (accumulator.playerMove!!.distance + accumulator.elephantMove!!.distance < element.playerMove!!.distance + element.elephantMove!!.distance)
                                accumulator
                            else
                                element
                        }
                        .values.toList()
                }

            val possibleSimulations = deduplicatedCombinations.map { it.toSimulation(this) }

            volcano.simulations.addAll(possibleSimulations)
            // start

            // println("Simulation on node ${currentValve}")
            // println(possibleSimulations.joinToString("\n"))

            if (volcano.bestScore < pressureReleased) {
                volcano.bestScore = pressureReleased
                println("New best score: $pressureReleased, path: ${path}")
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

        override fun compareTo(other: Simulation): Int {
            return other.pressureReleased.compareTo(this.pressureReleased)
        }

        override fun toString(): String {
            return "Simulation(remainingTime=$playerRemainingTime, pressureReleased=$pressureReleased, openValves=$openValves, currentValve=$currentValve, path=$path)"
        }
    }

    data class PossibleMovesCombination(val playerMove: PossibleMove?, val elephantMove: PossibleMove?) {
        fun toSimulation(parentSimulation: Simulation): Simulation {
            val simulation = Simulation(
                parentSimulation.playerRemainingTime - (playerMove?.getTimeCost() ?: 0),
                parentSimulation.elephantRemainingTime - (elephantMove?.getTimeCost() ?: 0),
                parentSimulation.pressureReleased
                        + (playerMove?.getOwnScore(parentSimulation.playerRemainingTime) ?: 0)
                        + (elephantMove?.getOwnScore(parentSimulation.elephantRemainingTime) ?: 0),
                parentSimulation.openValves
                        + (if (playerMove != null) setOf(playerMove.valve.identifier) else emptySet())
                        + (if (elephantMove != null) setOf(elephantMove.valve.identifier) else emptySet()),
                Pair(playerMove?.valve ?: Valve.NULL_VALVE, elephantMove?.valve ?: Valve.NULL_VALVE),
                parentSimulation.volcano,
                parentSimulation.path + Pair(
                    playerMove?.valve?.identifier ?: "null",
                    elephantMove?.valve?.identifier ?: "null"
                )
            )
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
        volcano.simulations.add(
            Simulation(
                30 - 4, 30 - 4, 0, mutableSetOf(), startingPoint to startingPoint, volcano, listOf(Pair("AA", "AA"))
            )
        )
        var counter = 0L
        while (volcano.simulations.isNotEmpty()) {
            val simulation = volcano.simulations.pollFirst()
            // println(simulation)
            simulation.start()
            if (counter++ % 100_000L == 0L) {
                println("Hello, these are your stats:")
                println("Top score: ${volcano.bestScore}")
                println("Simulations processed: $counter")
                println("Queue size: ${volcano.simulations.size}")
                println("Top score from top: ${volcano.simulations.first().pressureReleased}")
            }
        }
        return volcano.bestScore
    }

}

fun main() {
    val result = Puzzle16b().solveForFile()
    println("---")
    println(result)
}
