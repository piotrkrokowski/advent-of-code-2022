import org.junit.jupiter.api.Disabled

@Disabled
class Puzzle17bTest : BaseTestCase<Long>() {

    override fun expectedBaseTestCaseResult(): Long {
        //   419_999_999_731_200
        return 1_514_285_714_288L
//         return 3068
    }

    override fun instantiate(): Puzzle<Long> {
        return Puzzle17b()
        //return Puzzle17b(2022)
    }

}