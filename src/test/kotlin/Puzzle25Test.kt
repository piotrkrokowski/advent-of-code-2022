class Puzzle25Test : BaseTestCase<String>() {

    override fun expectedBaseTestCaseResult(): String {
        return "2=-1=0"
    }

    override fun instantiate(): Puzzle<String> {
        return Puzzle25()
    }
}