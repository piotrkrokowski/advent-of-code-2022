class Puzzle2Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 15
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle2()
    }

}