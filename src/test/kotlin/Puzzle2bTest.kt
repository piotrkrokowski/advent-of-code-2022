import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Puzzle2bTest {

    @Test
    fun testCaseFromExample() {
        val testedObject = Puzzle2b(
            listOf(
                "A Y",
                "B X",
                "C Z"
            )
        )
        val result = testedObject.solve()
        Assertions.assertEquals(12, result)
    }

}