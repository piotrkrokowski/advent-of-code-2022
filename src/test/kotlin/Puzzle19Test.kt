import Puzzle19.EnforcedDecisions
import Puzzle19.ResourceType.*
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle19Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 33
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle19()
    }

    @Test
    fun shouldProgressAsInTestCase() {
        val puzzle = Puzzle19(
            EnforcedDecisions()
                .allOtherRoundsNone()
                .inRound(3, CLAY)
                .inRound(5, CLAY)
                .inRound(7, CLAY)
                .inRound(11, OBSIDIAN)
                .inRound(12, CLAY)
                .inRound(15, OBSIDIAN)
                .inRound(18, GEODE)
                .inRound(21, GEODE)
        )

        puzzle.solve(
            listOf(
                "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian."
            )
        )

        assertThat(puzzle.blueprints[0].bestSimulation!!.getGeodes()).isEqualTo(9)
        assertThat(puzzle.blueprints[0].bestSimulation!!.robots).isEqualTo(
            mutableMapOf(
                Pair(ORE, 1),
                Pair(CLAY, 4),
                Pair(OBSIDIAN, 2),
                Pair(GEODE, 2),
            )
        )
        assertThat(puzzle.blueprints[0].bestSimulation!!.resources).isEqualTo(
            mutableMapOf(
                Pair(ORE, 6),
                Pair(CLAY, 41),
                Pair(OBSIDIAN, 8),
                Pair(GEODE, 9),
            )
        )
        assertThat(puzzle.blueprints[0].getScore()).isEqualTo(9)
    }
}