import Puzzle9b.*
import Puzzle9b.Direction.RIGHT
import Puzzle9b.Direction.UP
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle9bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 1
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle9b()
    }

    @Test
    fun shouldMoveVertically() {
        // given
        val rope = Rope()
        rope.moveHead(RIGHT)

        // when
        rope.moveHead(RIGHT)

        // then
        assertThat(rope.nodes[0].coords).isEqualTo(Coords(2, 0))
        assertThat(rope.nodes[1].coords).isEqualTo(Coords(1, 0))
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
        assertThat(rope.nodes[0].coords).isEqualTo(Coords(2, 1))
        assertThat(rope.nodes[1].coords).isEqualTo(Coords(1, 1))
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
        assertThat(rope.nodes[1].coords).isEqualTo(Coords(1, 1))
    }

    @Test
    fun testTwoComplexMoves() {
        // given
        val rope = Rope()

        // when
        MoveCommand(RIGHT, 5).execute(rope)
        MoveCommand(UP, 8).execute(rope)

        // then
        assertThat(rope.nodes[0].coords).isEqualTo(Coords(5, 8))
        assertThat(rope.nodes[1].coords).isEqualTo(Coords(5, 7))
        assertThat(rope.nodes[2].coords).isEqualTo(Coords(5, 6))
        assertThat(rope.nodes[3].coords).isEqualTo(Coords(5, 5))
        assertThat(rope.nodes[4].coords).isEqualTo(Coords(5, 4))
        assertThat(rope.nodes[5].coords).isEqualTo(Coords(4, 4))
    }


}