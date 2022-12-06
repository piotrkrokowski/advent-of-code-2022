import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Puzzle6bTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "mjqjpqmgbljsphdztnvjfqwrcgsmlb,19",
            "bvwbjplbgvbhsrlpgdmjqwftvncz, 23",
            "nppdvjthqldpwncqszvftbrmjlhg, 23",
            "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg, 29",
            "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw, 26"
        ]
    )
    fun exampleTestCases(inputString: String, expectedResult: Int) {
        val result = Puzzle6b().solve(listOf(inputString))

        assertThat(result).isEqualTo(expectedResult)
    }
}