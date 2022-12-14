class Puzzle14bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 93
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle14b()
    }

}