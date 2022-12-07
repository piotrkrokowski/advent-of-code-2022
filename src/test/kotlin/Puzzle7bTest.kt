class Puzzle7bTest : BaseTestCase<Int>() {

    override fun expectedBaseTestCaseResult(): Int {
        return 24933642
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle7b()
    }

}