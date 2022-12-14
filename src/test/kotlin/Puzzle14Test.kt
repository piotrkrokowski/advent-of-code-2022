import Puzzle14.Cave
import Puzzle14.Coords
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class Puzzle14Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 24
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle14()
    }

    @Test
    fun shouldDrawExample() {
        // given
        val cave = initCaveFromExampleInput()

        // when
        val result = drawExampleCave(cave)

        assertThat(result.trim()).isEqualTo(
            """
            ......+...
            ..........
            ..........
            ..........
            ....#...##
            ....#...#.
            ..###...#.
            ........#.
            ........#.
            #########.
            """.trimIndent()
        )
    }

    @Test
    fun shouldDropFirstSandBlock() {
        // given
        val cave = initCaveFromExampleInput()

        // when
        val sandBlock = cave.spawnSandBlock().drop()

        // then
        assertThat(sandBlock.isInAbyss()).isFalse()
        assertThat(sandBlock.coords).isEqualTo(Coords(500, 8))

        println(drawExampleCave(cave))
    }

    @Test
    fun testDrop22Units() {
        // given
        val cave = initCaveFromExampleInput()
        repeat(22) {
            cave.spawnSandBlock().drop()
        }

        // when
        val result = drawExampleCave(cave)

        // then
        assertThat(result.trim()).isEqualTo(
            """
            ......+...
            ..........
            ......o...
            .....ooo..
            ....#ooo##
            ....#ooo#.
            ..###ooo#.
            ....oooo#.
            ...ooooo#.
            #########.            
            """.trimIndent().trim()
        )
    }

    @Test
    fun shouldDrop25thToTheAbyss() {
        // given
        val cave = initCaveFromExampleInput()
        repeat(24) {
            cave.spawnSandBlock().drop()
        }

        // when
        println(drawExampleCave(cave))
        val sandBlock = cave.spawnSandBlock().drop()
        println(drawExampleCave(cave))

        // then
        assertThat(sandBlock.isInAbyss()).isTrue()
    }

    private fun initCaveFromExampleInput(): Cave {
        val lines = File("src/test/resources/puzzle14-example.txt").bufferedReader().readLines()
        val cave = Cave.fromLines(lines)
        return cave
    }

    //private fun drawExampleCave(cave: Cave) = cave.draw(494, 0, 503, 9)
    private fun drawExampleCave(cave: Cave) = cave.draw()


}