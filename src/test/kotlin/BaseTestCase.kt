import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class BaseTestCase<T> {
    @Test
    fun testCaseFromExample() {
        // given, when
        val result: T = instantiate().solveForFile { path ->
            path
                .replace("/main/", "/test/")
                .replace(".txt", "-example.txt")
        }

        // then
        assertThat(result).isEqualTo(expectedBaseTestCaseResult())
    }

    abstract fun expectedBaseTestCaseResult(): T

    abstract fun instantiate(): Puzzle<T>
}