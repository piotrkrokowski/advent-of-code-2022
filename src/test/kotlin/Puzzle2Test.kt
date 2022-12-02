class Puzzle2Test : BaseTestCase<Int>() {

    companion object {
        val PUZZLE_2_INPUT = listOf(
            "A Y",
            "B X",
            "C Z"
        )
    }

    override fun getExampleInput(): List<String> {
        return PUZZLE_2_INPUT
    }

    override fun expectedBaseTestCaseResult(): Int {
        return 15
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle2()
    }

}