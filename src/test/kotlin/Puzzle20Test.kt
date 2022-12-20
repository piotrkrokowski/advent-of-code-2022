class Puzzle20Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 3
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle20()
    }
}