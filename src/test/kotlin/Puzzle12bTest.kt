class Puzzle12bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 29
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle12b()
    }

}