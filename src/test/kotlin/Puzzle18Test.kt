class Puzzle18Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 64
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle18()
    }

}