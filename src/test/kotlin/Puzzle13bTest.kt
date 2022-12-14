class Puzzle13bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 140
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle13b()
    }

}