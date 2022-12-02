import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Puzzle2Test {

    @Test
    fun testCaseFromExample() {
        val testedObject = Puzzle2(
            listOf(
                "A Y",
                "B X",
                "C Z"
            )
        )
        val result = testedObject.solve()
        Assertions.assertEquals(15, result)
    }

}