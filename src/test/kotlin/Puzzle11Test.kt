class Puzzle11Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 10605
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle11()
    }

}