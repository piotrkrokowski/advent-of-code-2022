class Puzzle1Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 24000
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle1()
    }
}