class Puzzle21bTest : BaseTestCase<Long>() {

    override fun expectedBaseTestCaseResult(): Long {
        return 301
    }

    override fun instantiate(): Puzzle<Long> {
        return Puzzle21b()
    }

}