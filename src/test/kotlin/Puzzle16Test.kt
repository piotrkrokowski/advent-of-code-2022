class Puzzle16Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 1651
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle16()
    }

}