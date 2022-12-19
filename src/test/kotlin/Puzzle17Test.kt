class Puzzle17Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 3068
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle17()
    }

}