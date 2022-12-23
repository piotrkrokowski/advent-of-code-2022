class Puzzle23bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 20
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle23b()
    }


}
