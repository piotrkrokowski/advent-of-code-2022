import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Puzzle1bTest : BaseTestCase<Int>() {

    @Test
    fun shouldPromoteInTheMiddle() {
        // given
        val input = listOf(
            "30",
            "",
            "20",
            "",
            "10",
            "",
            "25"
        )

        // when
        val result = Puzzle1b().solve(input)

        // then
        assertEquals(75, result)
    }

    override fun getExampleInput(): List<String> {
        return Puzzle1Test.PUZZLE_1_INPUT
    }

    override fun expectedBaseTestCaseResult(): Int {
        return 45000
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle1b()
    }
}