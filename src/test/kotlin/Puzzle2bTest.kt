class Puzzle2bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 12
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle2b()
    }

}