class Puzzle10bTest : BaseTestCase<String>() {

    override fun expectedBaseTestCaseResult(): String {
        return """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
            """.trimIndent()

    }

    override fun instantiate(): Puzzle<String> {
        return Puzzle10b()
    }

}