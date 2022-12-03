class Puzzle3bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 70
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle3b()
    }

}