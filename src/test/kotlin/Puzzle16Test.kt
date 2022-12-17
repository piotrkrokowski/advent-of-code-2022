import java.io.File

class Puzzle16Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 1651
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle16()
    }

    private fun initFromExampleInput(): Puzzle16.Volcano {
        val lines = File("src/test/resources/puzzle16-example.txt").bufferedReader().readLines()
        return Puzzle16.Volcano.fromLines(lines)
    }
}