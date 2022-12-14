import Puzzle13.SignalList
import Puzzle13.SingleSignal
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle13Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 13
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle13()
    }

    @Test
    fun shouldCompareLists() {
        // given
        val list1 = SignalList(
            listOf(
                SignalList(listOf(SingleSignal(1))),
                SignalList(listOf(SingleSignal(2), SingleSignal(3), SingleSignal(4)))
            )
        )
        val list2 = SignalList(listOf((SignalList(listOf(SingleSignal(1)))), SingleSignal(4)))

        // when
        val result = list1.compareTo(list2)

        assertThat(result).isEqualTo(-1)
    }

    @Test
    fun shouldReturnMinus1WhenFirstListRanOutOfElements() {
        // given
        val list1 = SignalList(listOf())
        val list2 = SignalList(listOf(SingleSignal(3)))

        // when
        val result = list1.compareTo(list2)

        assertThat(result).isEqualTo(-1)
    }

    @Test
    fun shouldReturnPlus1WhenSecondListRanOutOfElements() {
        // given
        val list1 = SignalList(listOf(SingleSignal(3)))
        val list2 = SignalList(listOf())

        // when
        val result = list1.compareTo(list2)

        assertThat(result).isEqualTo(1)
    }

    @Test
    fun shouldReturnZeroWhenBothListsRanOutOfElements() {
        // given
        val list1 = SignalList(listOf())
        val list2 = SignalList(listOf())

        // when
        val result = list1.compareTo(list2)

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun shouldParseList() {
        // given
        val input = "[1,[2,3],4,5,[],[6,[7,8]],[[9]]]"

        // when
        val result = SignalList.fromString(input)

        assertThat(result).isEqualTo(
            SignalList(
                listOf(
                    SingleSignal(1),
                    SignalList(
                        listOf(
                            SingleSignal(2),
                            SingleSignal(3),
                        )
                    ),
                    SingleSignal(4),
                    SingleSignal(5),
                    SignalList(listOf()),
                    SignalList(
                        listOf(
                            SingleSignal(6),
                            SignalList(
                                listOf(
                                    SingleSignal(7),
                                    SingleSignal(8),
                                )
                            )
                        )
                    ),
                    SignalList(
                        listOf(
                            SignalList(
                                listOf(
                                    SingleSignal(9)
                                )
                            )
                        )
                    )
                )
            )
        )

    }
}