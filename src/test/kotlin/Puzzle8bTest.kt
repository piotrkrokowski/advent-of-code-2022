import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class Puzzle8bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 8
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle8b()
    }

    @Test
    @Disabled // private test data
    fun testCaseFromExample2() {
        // given, when
        val result = instantiate().solveForFile()

        // then
        assertThat(result).isEqualTo(496125)
    }

}