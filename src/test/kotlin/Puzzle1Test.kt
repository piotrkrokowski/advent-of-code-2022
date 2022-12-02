class Puzzle1Test : BaseTestCase<Int>() {

    companion object {
        internal val PUZZLE_1_INPUT = listOf(
            "1000",
            "2000",
            "3000",
            "",
            "4000",
            "",
            "5000",
            "6000",
            "",
            "7000",
            "8000",
            "9000",
            "",
            "10000"
        )
    }

    override fun getExampleInput(): List<String> {
        return PUZZLE_1_INPUT
    }

    override fun expectedBaseTestCaseResult(): Int {
        return 24000
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle1()
    }
}