import Puzzle22.*
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle22Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 6032
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle22()
    }

    @Test
    fun testParseAndDrawMaze() {
        val lines = readLines().map { it.padEnd(16, ' ') }.dropLast(2)
        val expectedMaze = lines.joinToString("\n")

        val maze = Maze.build(lines)
        val result = maze.toString().replace('@', '.')

        assertThat(result.trimEnd()).isEqualTo(expectedMaze.trimEnd())
    }

    @Test
    fun shouldParseOrders() {
        val orders = Orders.fromString("10R5L")

        assertThat(orders.orders).containsExactly(
            MoveOrder(10),
            TurnOrder(1),
            MoveOrder(5),
            TurnOrder(-1)
        )
    }

    @Test
    fun testRealPuzzle() {
        val result = Puzzle22().solveForFile()

        assertThat(result).isEqualTo(95358)
    }


}