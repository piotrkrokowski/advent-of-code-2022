class Puzzle16bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 1707
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle16b()
    }

}