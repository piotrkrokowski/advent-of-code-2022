class Puzzle4bTest : BaseTestCase<Int>() {

    override fun pathModifier(path: String): String {
        return "src/test/resources/puzzle4b-example.txt"
    }

    override fun expectedBaseTestCaseResult(): Int {
        return 4
    }

    override fun instantiate(): Puzzle<Int> {
        return Puzzle4b()
    }

}