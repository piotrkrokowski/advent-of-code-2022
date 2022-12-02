import java.io.File

class Puzzle2b(private var lines: List<String>) {

    enum class Result(val score: Int, val goalString: String) {
        WIN(6, "Z"),
        DRAW(3, "Y"),
        LOOSE(0, "X");

        fun findMoveForOpponentMove(opponentMove: Move): Move {
            return if (this == LOOSE) opponentMove.getMoveWhichLoosesWithThisOne()
            else if (this == DRAW) return opponentMove
            else opponentMove.getMoveWhichLoosesWithThisOne().getMoveWhichLoosesWithThisOne() // hehe
        }

        companion object {
            fun fromString(string: String): Result {
                return values().first { it.goalString == string }
            }
        }
    }

    enum class Move(val opponentCode: String, val score: Int) {
        ROCK("A", 1) {
            override fun getMoveWhichLoosesWithThisOne(): Move {
                return SCISSORS
            }
        },
        PAPER("B", 2) {
            override fun getMoveWhichLoosesWithThisOne(): Move {
                return ROCK
            }
        },
        SCISSORS("C", 3) {
            override fun getMoveWhichLoosesWithThisOne(): Move {
                return PAPER
            }
        };

        fun beats(move: Move): Boolean {
            return this.getMoveWhichLoosesWithThisOne() == move
        }

        abstract fun getMoveWhichLoosesWithThisOne(): Move

        fun resolve(move: Move): Result {
            return if (this.beats(move)) Result.WIN
            else if (move.beats(this)) Result.LOOSE
            else Result.DRAW
        }

        companion object {
            fun opponentMoveFromString(opponentCode: String): Move {
                return values().first { it.opponentCode.equals(opponentCode) }
            }

            fun playerGoalFromString(playerGoalString: String): Result {
                return Result.fromString(playerGoalString)
            }
        }
    }

    data class Round(private val opponentMove: Move, private val playerMove: Move) {

        fun getScore(): Int {
            return playerMove.score + playerMove.resolve(opponentMove).score
        }

        companion object {
            fun fromString(string: String): Round {
                val split = string.split(" ")
                val goal: Result = Move.playerGoalFromString(split[1])
                val opponentMove = Move.opponentMoveFromString(split[0])
                val playerMove = goal.findMoveForOpponentMove(opponentMove)
                val round = Round(opponentMove, playerMove)
                println("${round}: ${round.getScore()}")
                return round
            }
        }
    }

    fun solve(): Int {
        return lines.map { Round.fromString(it).getScore() }.sum()
    }

}

fun main() {
    val lines = File("src/main/resources/puzzle2.txt").readLines()
    val puzzle = Puzzle2b(lines)
    val result = puzzle.solve()
    println("---")
    println(result)
}