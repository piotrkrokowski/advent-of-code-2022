import Puzzle9.Coords
import Puzzle9.Direction.RIGHT
import Puzzle9.Direction.UP
import Puzzle9.Rope
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle9Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 13
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle9()
    }

    @Test
    fun shouldMoveVertically() {
        // given
        val rope = Rope()
        rope.moveHead(RIGHT)

        // when
        rope.moveHead(RIGHT)

        // then
        assertThat(rope.tail).isEqualTo(Coords(1, 0))
    }

    @Test
    fun shouldMoveDiagonallyRight() {
        // given
        val rope = Rope()
        rope.moveHead(RIGHT)
        rope.moveHead(UP)

        // when
        rope.moveHead(RIGHT)

        // then
        assertThat(rope.tail).isEqualTo(Coords(1, 1))
    }

    @Test
    fun shouldMoveDiagonallyUp() {
        // given
        val rope = Rope()
        rope.moveHead(RIGHT)
        rope.moveHead(UP)

        // when
        rope.moveHead(RIGHT)

        // then
        assertThat(rope.tail).isEqualTo(Coords(1, 1))
    }
}