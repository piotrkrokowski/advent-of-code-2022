class Puzzle5bTest : BaseTestCase<String>() {

    override fun expectedBaseTestCaseResult(): String {
        return "MCD"
    }

    override fun instantiate(): Puzzle<String> {
        return Puzzle5b()
    }

}