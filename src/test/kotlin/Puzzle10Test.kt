class Puzzle10Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 13140
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle10()
    }

}