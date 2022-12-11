class Puzzle11bTest : BaseTestCase<Long>() {

    override fun expectedBaseTestCaseResult(): Long {
        return 2713310158L
    }

    override fun instantiate(): Puzzle<Long> {
        return Puzzle11b()
    }
}