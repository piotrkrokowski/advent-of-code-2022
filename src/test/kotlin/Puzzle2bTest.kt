class Puzzle2bTest : BaseTestCase<Int>() {

    // 12
    override fun getExampleInput(): List<String> {
        return Puzzle2Test.PUZZLE_2_INPUT
    }

    override fun expectedBaseTestCaseResult(): Int {
        return 12
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle2b()
    }

}