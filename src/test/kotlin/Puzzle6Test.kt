import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Puzzle6Test {

    @ParameterizedTest
    @CsvSource(
        value = [
            "mjqjpqmgbljsphdztnvjfqwrcgsmlb,7",
            "bvwbjplbgvbhsrlpgdmjqwftvncz, 5",
            "nppdvjthqldpwncqszvftbrmjlhg, 6",
            "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg, 10",
            "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw, 11"
        ]
    )
    fun exampleTestCases(inputString: String, expectedResult: Int) {
        val result = Puzzle6().solve(listOf(inputString))

        assertThat(result).isEqualTo(expectedResult)
    }
}