class Puzzle21Test : BaseTestCase<Long>() {

    override fun expectedBaseTestCaseResult(): Long {
        return 152
    }

    override fun instantiate(): Puzzle<Long> {
        return Puzzle21()
    }

}