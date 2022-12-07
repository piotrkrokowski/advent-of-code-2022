class Puzzle7Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 95437
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle7()
    }

}