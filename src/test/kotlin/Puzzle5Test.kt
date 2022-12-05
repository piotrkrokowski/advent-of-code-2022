class Puzzle5Test : BaseTestCase<String>() {

    override fun expectedBaseTestCaseResult(): String {
        return "CMZ"
    }

    override fun instantiate(): Puzzle<String> {
        return Puzzle5()
    }

}