import Puzzle15b.Line
import Puzzle15b.Range
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle15Test : BaseTestCase<Long>() {

    override fun expectedBaseTestCaseResult(): Long {
        return 56000011
    }

    override fun instantiate(): Puzzle<Long> {
        return Puzzle15b(20)
    }

    @Test
    fun shouldCombineRanges() {
        // given
        val r1 = Range(5, 10)
        val r2 = Range(6, 7)
        val r3 = Range(15, 20)
        val r4 = Range(10, 15)
        val r5 = Range(-5, 100)

        val line = Line(0)

        // expect
        line.addRange(r1)
        assertThat(line.ranges).containsOnly(Range(5, 10))
        line.addRange(r2)
        assertThat(line.ranges).containsOnly(Range(5, 10))
        line.addRange(r3)
        assertThat(line.ranges).containsOnly(Range(5, 10), Range(15, 20))
        line.addRange(r4)
        assertThat(line.ranges).containsOnly(Range(5, 20))
        line.addRange(r5)
        assertThat(line.ranges).containsOnly(Range(-5, 100))
    }
}