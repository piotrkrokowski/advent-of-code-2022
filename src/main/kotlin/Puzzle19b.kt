import Puzzle19b.ResourceType.*

/**
 * OK, it kind of earned me the gold start, but it's HORRIBLE.
 * TODOs for future improvements:
 * 1. Clean up the code from debugging stuff and optimize calls
 * 2. Optimize for robot shopping decisions, possibly jump straight few turns forwards, precalculating resources in the future
 * 3. Implement multi-threading for branch analysis. During the execution CPU usage is merely a couple of %, it's mostly running on single core. Such a waste.
 * 4. Maybe improve branch cutoff strategy in case of "no hope" to produce anything
 */
class Puzzle19b(private val enforceDecisions: EnforcedDecisions = EnforcedDecisions()) : Puzzle<Int> {

    companion object {
        val ROUNDS = 32
    }

    enum class ResourceType {
        ORE, CLAY, OBSIDIAN, GEODE;
    }

    data class Cost(
        private val cost: Map<ResourceType, Int>
    ) {
        fun getCost(resourceType: ResourceType): Int {
            return cost[resourceType] ?: 0
        }
    }

    class Blueprint(
        val id: Int,
        val robotCost: Map<ResourceType, Cost>,
        private val enforceDecisions: EnforcedDecisions
    ) {
        var bestSimulation: Simulation? = null

        fun evaluate() {
            val initialSimulation = Simulation(this, 1, STARTING_RESOURCES, STARTING_ROBOTS, Array(ROUNDS + 1) { 0 }, emptyList(), enforceDecisions, emptyList())
            bestSimulation = initialSimulation
            bestSimulation = initialSimulation.run()
        }

        fun getScore(): Int {
            return bestSimulation!!.getGeodes()
        }

        fun checkIfBest(bestSimulationFromBranch: Simulation) {
            if (bestSimulationFromBranch.getGeodes() > bestSimulation!!.getGeodes()) {
                this.bestSimulation = bestSimulationFromBranch
                println("New best score: ${bestSimulationFromBranch.getGeodes()}, $bestSimulationFromBranch")
            }
        }

        companion object {
            private val REGEX =
                Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")

            val STARTING_RESOURCES: Map<ResourceType, Int> = mutableMapOf(
                Pair(ORE, 0), Pair(CLAY, 0), Pair(OBSIDIAN, 0), Pair(GEODE, 0)
            )

            val STARTING_ROBOTS: Map<ResourceType, Int> = mutableMapOf(
                Pair(ORE, 1), Pair(CLAY, 0), Pair(OBSIDIAN, 0), Pair(GEODE, 0)
            )

            fun fromString(line: String, enforceDecisions: EnforcedDecisions): Blueprint {
                val found = REGEX.find(line)!!
                with(found) {
                    return Blueprint(
                        groupValues[1].toInt(),
                        mapOf(
                            Pair(ORE, Cost(mapOf(Pair(ORE, groupValues[2].toInt())))),
                            Pair(CLAY, Cost(mapOf(Pair(ORE, groupValues[3].toInt())))),
                            Pair(OBSIDIAN, Cost(mapOf(Pair(ORE, groupValues[4].toInt()), Pair(CLAY, groupValues[5].toInt())))),
                            Pair(GEODE, Cost(mapOf(Pair(ORE, groupValues[6].toInt()), Pair(OBSIDIAN, groupValues[7].toInt()))))
                        ),
                        enforceDecisions
                    )
                }
            }
        }
    }

    class EnforcedDecisions {
        private val roundToDecision: MutableMap<Int, ResourceType?> = mutableMapOf()

        fun isEnforced(round: Int): Boolean {
            return roundToDecision.containsKey(round)
        }

        fun get(round: Int): ResourceType? {
            return roundToDecision[round]
        }
    }

    class Simulation(
        private val blueprint: Blueprint,
        val round: Int,
        val resources: Map<ResourceType, Int>,
        val robots: Map<ResourceType, Int>,
        val geodesInRound: Array<Int>,
        private val decisions: List<ResourceType?>,
        private val enforceDecisions: EnforcedDecisions,
        private val possibleRobotsToBuyFromPreviousRound: List<ResourceType?>,
    ) : Comparable<Simulation> {

        fun run(): Simulation? {
            /**
             * Optimisations:
             * 1. Do not buy anything on last round
             * 2. On 23 buy only geodes
             * 3. On 22 buy only geodes or (ore or obsidian if enough ore to buy geode at end)
             */
            if (round > ROUNDS) return this
            if (blueprint.bestSimulation!!.getGeodes() > maxThisCouldAchieveTheoretically()) {
                return null
            } // Won't beat best simulation
            if (blueprint.bestSimulation!!.geodesInRound[round] > resources[GEODE]!! + robots[GEODE]!! + 1) { // XD :P
                //println("EL2")
                return null
            } // Won't beat best simulation
            val possibleRobotsToBuy: List<ResourceType?> = computeRobotsToBuyOptions()
            val newResources: Map<ResourceType, Int> = resources.mapValues { resources[it.key]!! + robots[it.key]!! }
            geodesInRound[round] = newResources[GEODE]!!
            val buildNewSimulations = buildNewSimulations(possibleRobotsToBuy, newResources)
            val bestSimulation = buildNewSimulations.map { it.run() }.sortedByDescending { it?.getGeodes() }.firstOrNull()
            if (bestSimulation != null) {
                blueprint.checkIfBest(bestSimulation)
            }
            return bestSimulation
        }

        private fun maxThisCouldAchieveTheoretically(): Int {
            return getGeodes() + maxPossibleGeodesProducedUntilEnd()
        }

        private fun maxPossibleGeodesProducedUntilEnd(): Int {
            val remainingRounds = getRemainingRounds()
            return (remainingRounds * (2 * robots[GEODE]!! + (remainingRounds - 1))) / 2
        }

        private fun getRemainingRounds() = (ROUNDS - (round - 1))

        private fun computeRobotsToBuyOptions(): List<ResourceType?> {
            val affordableRobots = affordableRobots()
            if (enforceDecisions.isEnforced(round)) {
                return limitToEnforcedDecision(affordableRobots)
            } else {
                return chooseBestRobot(affordableRobots)
            }
        }

        private fun chooseBestRobot(affordableRobots: List<ResourceType>): List<ResourceType?> {
            if (round == ROUNDS) return listOf(null) // Don't ever buy anything in last round
            if (affordableRobots.isNotEmpty()) {
                if (GEODE in affordableRobots) return listOf(GEODE) // ALWAYS buy Geode!
                if (decisions[round - 2] == null && possibleRobotsToBuyFromPreviousRound.filterNotNull().toSet() == affordableRobots.toSet()) {
                    // Continue saving
                    return listOf(null)
                }
                val robotsThatMakeSense = affordableRobots.filter { doesRobotMakeSenseInRound(it) }
                return (robotsThatMakeSense + null)
            } else return listOf(null)
        }

        private fun doesRobotMakeSenseInRound(robotType: ResourceType): Boolean {
            if (round == ROUNDS - 1)
                return robotType == GEODE
            else if (round == ROUNDS - 2) {
                return robotType == GEODE || (robotType in listOf(ORE, OBSIDIAN) && resources[ORE]!! - blueprint.robotCost[robotType]!!.getCost(ORE) >= blueprint.robotCost[robotType]!!.getCost(GEODE))
            }
            return true
        }

        private fun affordableRobots(): List<ResourceType> {
            return values().filter { canAffordRobot(it) }
        }

        private fun limitToEnforcedDecision(possibleRobotsToBuy: List<ResourceType>): MutableList<ResourceType?> {
            val enforcedDecision = enforceDecisions.get(round)
            println("Enforcing decision $enforcedDecision")
            return if (enforcedDecision != null) {
                if (!possibleRobotsToBuy.contains(enforcedDecision)) throw IllegalStateException()
                mutableListOf(enforcedDecision)
            } else {
                mutableListOf(null)
            }
        }

        private fun buildNewSimulations(possibleRobotsToBuy: List<ResourceType?>, newResources: Map<ResourceType, Int>): List<Simulation> {
            return possibleRobotsToBuy.map {
                Simulation(blueprint, round + 1, subtractResources(it, newResources), addRobot(it, robots), geodesInRound, decisions + it, enforceDecisions, possibleRobotsToBuy)
            }
        }

        private fun subtractResources(robot: ResourceType?, resources: Map<ResourceType, Int>): Map<ResourceType, Int> {
            if (robot == null) return resources
            return resources.mapValues { it.value - blueprint.robotCost[robot]!!.getCost(it.key) }
        }

        private fun addRobot(newRobot: ResourceType?, robots: Map<ResourceType, Int>): Map<ResourceType, Int> {
            return robots.mapValues { it.value + if (newRobot == it.key) 1 else 0 }
        }

        private fun canAffordRobot(robot: ResourceType): Boolean {
            return resources.all { blueprint.robotCost[robot]!!.getCost(it.key) <= it.value }
        }

        override fun compareTo(other: Simulation): Int {
            return getGeodes().compareTo(other.getGeodes())
        }

        fun getGeodes(): Int {
            return resources[GEODE]!!
        }

        override fun toString(): String {
            return "Simulation(round=$round, score=${getGeodes()}, decisions=$decisions)"
        }
    }

    var blueprints: List<Blueprint> = emptyList()

    override fun solve(lines: List<String>): Int {
        blueprints = lines.take(3).map { Blueprint.fromString(it, enforceDecisions) }
        for (blueprint in blueprints) {
            println("Evaluating blueprint ${blueprint.id}")
            blueprint.evaluate()
        }
        return blueprints.map { it.getScore() }.reduce { acc, i -> acc * i }
    }
}

fun main() {
    val result = Puzzle19b().solveForFile()
    println("---")
    println(result)
}