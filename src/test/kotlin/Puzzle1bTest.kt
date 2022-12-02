import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Puzzle1bTest {

    @Test
    fun shouldPromoteInTheMiddle() {
        val testedObject = Puzzle1b(
            listOf(
                "30",
                "",
                "20",
                "",
                "10",
                "",
                "25"
            )
        )
        val result = testedObject.solve()
        assertEquals(75, result)
    }
}