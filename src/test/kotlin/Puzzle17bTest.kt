class Puzzle17bTest : BaseTestCase<Long>() {

    override fun expectedBaseTestCaseResult(): Long {
        // return 1514285714288
        return 3068
    }

    override fun instantiate(): Puzzle<Long> {
        //return Puzzle17b(Puzzle17b.ITERATIONS)
        return Puzzle17b(2022)
    }

}