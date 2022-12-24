class Puzzle19bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 56 * 62
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle19b()
    }
}