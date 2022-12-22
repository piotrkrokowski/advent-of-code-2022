// This became a mess :)
class Puzzle16 : Puzzle<Int> {

    class Volcano : Graph<Valve>() {

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
    ) : Graph.Node<Valve>(code) {

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
        var remainingTime: Int,
        var pressureReleased: Int,
        var openValves: Set<String> = mutableSetOf(),
        val currentValve: Valve,
        val volcano: Volcano,
    ) {
        private var choicesSequence: List<String> = emptyList()

        fun start(): Simulation {
            val possibleMoves = volcano.getAllPathsFrom(currentValve.identifier)
                .asSequence()
                .filter { it.nodePair.to.flow > 0 }
                .filter { it.nodePair.to != currentValve }
                .filter { it.nodePair.to.identifier !in openValves }
                .map { path -> PossibleMove(path.nodePair.to, path.distance) }
                .filter { it.getTimeCost() <= remainingTime }
                .toList()

            if (possibleMoves.isEmpty()) return this

            val possibleSimulations = possibleMoves
                .map { it.runSimulation(this) }
                .sortedByDescending { it.pressureReleased }

            // println("Simulation on node ${currentValve.identifier}")
            // println(possibleSimulations.joinToString("\n"))
            val bestSimulation = possibleSimulations[0]
            // println("Choosing $bestSimulation")

            this.merge(bestSimulation)

            return this
        }

        private fun merge(bestSimulation: Simulation) {
            choicesSequence = listOf(bestSimulation.currentValve.identifier) + bestSimulation.choicesSequence
            remainingTime = bestSimulation.remainingTime
            openValves = bestSimulation.openValves
            pressureReleased = bestSimulation.pressureReleased
        }

        override fun toString(): String {
            return "Simulation(remainingTime=$remainingTime, pressureReleased=$pressureReleased, openValves=$openValves, currentValve=$currentValve, choicesSequence=$choicesSequence)"
        }
    }

    data class PossibleMove(val valve: Valve, val distance: Int) {

        fun runSimulation(parentSimulation: Simulation): Simulation {
            val simulation = Simulation(
                parentSimulation.remainingTime - getTimeCost(),
                parentSimulation.pressureReleased + getOwnScore(parentSimulation.remainingTime),
                parentSimulation.openValves + valve.identifier,
                valve,
                parentSimulation.volcano
            )
            simulation.start()
            return simulation
        }

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
        val simulation = Simulation(30, 0, mutableSetOf(), volcano.get("AA")!!, volcano).start()
        return simulation.pressureReleased
    }


}

fun main() {
    val result = Puzzle16().solveForFile()
    println("---")
    println(result)
}
