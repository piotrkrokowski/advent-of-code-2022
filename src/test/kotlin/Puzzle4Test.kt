class Puzzle4Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 2
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle4()
    }

}