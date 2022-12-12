class Puzzle12Test : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 31
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle12()
    }

}