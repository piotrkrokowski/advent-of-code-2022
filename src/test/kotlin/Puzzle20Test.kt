import Puzzle20.Solver
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class Puzzle20Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 3
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle20()
    }

    @Test
    fun shouldMoveRight() {
        val solver = Solver(listOf(1000, 1, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, 1002, 1, 1003))
    }

    @Test
    fun shouldMoveLeft() {
        val solver = Solver(listOf(1000, 1001, -1, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, -1, 1001, 1003))
    }

    @Test
    fun shouldMoveRightToEnd() {
        val solver = Solver(listOf(1000, 2, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, 1002, 1003, 2))
    }

    @Test
    fun shouldMoveLeftToEnd() {
        val solver = Solver(listOf(1000, -1, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(-1, 1000, 1002, 1003))
    }

    @Test
    fun shouldMoveRightWithEdgeOverlap() {
        val solver = Solver(listOf(1000, 3, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, 3, 1002, 1003))
    }

    @Test
    fun shouldMoveLeftWithEdgeOverlap() {
        val solver = Solver(listOf(1000, -2, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, 1002, -2, 1003))
    }

    @Test
    fun shouldMoveRightWithModuloOverlapPreviousSpot() {
        val solver = Solver(listOf(1000, 11, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, 1002, 1003, 11))
    }

    @Test
    fun shouldMoveRightWithModuloOverlapSameSpot() {
        val solver = Solver(listOf(1000, 12, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, 12, 1002, 1003))
    }

    @Test
    fun shouldMoveLeftWithModuloOverlap() {
        val solver = Solver(listOf(1000, -11, 1002, 1003), 999)
        solver.solve()
        assertThat(solver.getNumbers()).isEqualTo(arrayOf(1000, 1002, -11, 1003))
    }

    @Test
    fun shouldGetResultSimple() {
        val solver = Solver((0..5000).toList(), -100)
        val result = solver.solve()
        assertThat(result).isEqualTo(1000 + 2000 + 3000)
    }

    @Test
    fun shouldGetResultWhenZeroIsNotFirst() {
        val solver = Solver((-1..5000).toList(), -100)
        val result = solver.solve()
        assertThat(result).isEqualTo(1000 + 2000 + 3000)
    }

    @Test
    fun shouldGetResultWithOverlap() {
        val solver = Solver((0..1000).toList(), -100)
        val result = solver.solve()
        assertThat(result).isEqualTo(1000 + 999 + 998)
    }


}