import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigInteger
import java.util.stream.Stream

class SnafuNumberTest {

    @ParameterizedTest
    @MethodSource("decimalToSnafu")
    fun shouldConvertToDecimal(decimal: Int, snafu: String) {
        val snafuNumber = SnafuNumber(snafu)

        val result = snafuNumber.toDecimal().toInt()

        assertThat(result).isEqualTo(decimal)
    }

    @ParameterizedTest
    @MethodSource("decimalToSnafu")
    fun shouldConvertFromDecimal(decimal: Long, snafu: String) {
        val result = SnafuNumber.fromDecimal(BigInteger.valueOf(decimal))

        assertThat(result).isEqualTo(SnafuNumber(snafu))
    }

    companion object {
        @JvmStatic
        fun decimalToSnafu(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(1, "1"),
                Arguments.of(2, "2"),
                Arguments.of(3, "1="),
                Arguments.of(4, "1-"),
                Arguments.of(5, "10"),
                Arguments.of(6, "11"),
                Arguments.of(7, "12"),
                Arguments.of(8, "2="),
                Arguments.of(9, "2-"),
                Arguments.of(10, "20"),
                Arguments.of(15, "1=0"),
                Arguments.of(20, "1-0"),
                Arguments.of(2022, "1=11-2"),
                Arguments.of(12345, "1-0---0"),
                Arguments.of(314159265, "1121-1110-1=0"),
                Arguments.of(1747, "1=-0-2"),
                Arguments.of(906, "12111"),
                Arguments.of(198, "2=0="),
                Arguments.of(11, "21"),
                Arguments.of(201, "2=01"),
                Arguments.of(31, "111"),
                Arguments.of(1257, "20012"),
                Arguments.of(32, "112"),
                Arguments.of(353, "1=-1="),
                Arguments.of(107, "1-12"),
                Arguments.of(37, "122")
            )
        }
    }


}