class Puzzle18bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 58
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle18b()
    }

}