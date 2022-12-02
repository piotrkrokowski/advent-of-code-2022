class Puzzle2 : Puzzle<Int> {

    enum class Result(val score: Int) {
        WIN(6),
        DRAW(3),
        LOOSE(0);
    }

    enum class Move(val opponentCode: String, val playerCode: String, val score: Int) {
        ROCK("A", "X", 1) {
            override fun beats(move: Move): Boolean {
                return move == SCISSORS
            }
        },
        PAPER("B", "Y", 2) {
            override fun beats(move: Move): Boolean {
                return move == ROCK
            }
        },
        SCISSORS("C", "Z", 3) {
            override fun beats(move: Move): Boolean {
                return move == PAPER
            }
        };

        abstract fun beats(move: Move): Boolean

        fun resolve(move: Move): Result {
            return if (this.beats(move)) Result.WIN
            else if (move.beats(this)) Result.LOOSE
            else Result.DRAW
        }

        companion object {
            fun opponentMoveFromString(opponentCode: String): Move {
                return values().first { it.opponentCode == opponentCode }
            }

            fun playerMoveFromString(playerCode: String): Move {
                return values().first { it.playerCode == playerCode }
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
                val round = Round(Move.opponentMoveFromString(split[0]), Move.playerMoveFromString(split[1]))
                // println("${round}: ${round.getScore()}")
                return round
            }
        }
    }

    override fun solve(lines: List<String>): Int {
        return lines.map { Round.fromString(it).getScore() }.sum()
    }

}

fun main() {
    val result = Puzzle2().solveForFile()
    println("---")
    println(result)
}