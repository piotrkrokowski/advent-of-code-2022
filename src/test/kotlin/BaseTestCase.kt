import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class BaseTestCase<T> {
    @Test
    fun testCaseFromExample() {
        // given, when
        val result: T = instantiate().solveForFile { path ->
            pathModifier(path)
        }

        // then
        assertThat(result).isEqualTo(expectedBaseTestCaseResult())
    }

    protected open fun pathModifier(path: String): String = path
        .replace("/main/", "/test/")
        .replace(".txt", "-example.txt")

//  Same thing, another way:
//    protected open fun pathModifier(path: String):String = path
//        .replace("/main/", "/test/")
//        .replace(".txt", "-example.txt")

    abstract fun expectedBaseTestCaseResult(): T

    abstract fun instantiate(): Puzzle<T>
}