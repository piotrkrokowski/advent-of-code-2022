import Puzzle24.Valley
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle24bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 54
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle24b()
    }

    @Test
    fun shouldSimulateBlizzardsAsInExample() {
        val valley = Valley.fromLines(
            """
            #.#####
            #.....#
            #>....#
            #.....#
            #...v.#
            #.....#
            #####.#
        """.trimIndent().lines()
        )

        val valleysInTurns = (0..5).map { valley.getValleyInTurn(it).toString().trim() }.toList()

        assertThat(valleysInTurns[0]).isEqualTo(
            """
            #.#####
            #.....#
            #>....#
            #.....#
            #...v.#
            #.....#
            #####.#
        """.trimIndent()
        )

        assertThat(valleysInTurns[1]).isEqualTo(
            """
            #.#####
            #.....#
            #.>...#
            #.....#
            #.....#
            #...v.#
            #####.#
        """.trimIndent()
        )

        assertThat(valleysInTurns[2]).isEqualTo(
            """
            #.#####
            #...v.#
            #..>..#
            #.....#
            #.....#
            #.....#
            #####.#
        """.trimIndent()
        )

        assertThat(valleysInTurns[3]).isEqualTo(
            """
            #.#####
            #.....#
            #...2.#
            #.....#
            #.....#
            #.....#
            #####.#
        """.trimIndent()
        )

        assertThat(valleysInTurns[4]).isEqualTo(
            """
            #.#####
            #.....#
            #....>#
            #...v.#
            #.....#
            #.....#
            #####.#
        """.trimIndent()
        )

        assertThat(valleysInTurns[5]).isEqualTo(
            """
            #.#####
            #.....#
            #>....#
            #.....#
            #...v.#
            #.....#
            #####.#
        """.trimIndent()
        )
    }

    @Test
    fun shouldWrapBlizzardInOppositeDirection() {
        val valley = Valley.fromLines(
            """
            #.#####
            #...^.#
            #<....#
            #.....#
            #.....#
            #.....#
            #####.#
        """.trimIndent().lines()
        )

        val valleyNextTurn = valley.getValleyInTurn(1).toString().trim()

        assertThat(valleyNextTurn).isEqualTo(
            """
            #.#####
            #.....#
            #....<#
            #.....#
            #.....#
            #...^.#
            #####.#
        """.trimIndent()
        )
    }
}