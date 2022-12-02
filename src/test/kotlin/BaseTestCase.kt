import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class BaseTestCase<T> {
    @Test
    fun testCaseFromExample() {
        // given, when
        val result: T = instantiate().solve(getExampleInput())

        // then
        assertThat(result).isEqualTo(expectedBaseTestCaseResult())
    }

    abstract fun getExampleInput(): List<String>

    abstract fun expectedBaseTestCaseResult(): T

    abstract fun instantiate(): Puzzle<T>
}