class Puzzle3Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 157
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle3()
    }

}