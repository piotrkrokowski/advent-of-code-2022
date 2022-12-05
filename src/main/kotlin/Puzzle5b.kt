class Puzzle5b : Puzzle5() {

    class GroupMove(quantity: Int, from: Int, to: Int) : Move(quantity, from, to) {
        override fun applyTo(stacks: Stacks) {
            // It could have been more optimal... but I just realise AoC is not really about performance ;]
            val buffer: ArrayDeque<Char> = ArrayDeque()
            repeat(quantity) {
                buffer.addFirst(stacks.getFromTop(from))
            }
            repeat(quantity) {
                stacks.addOnTop(to, buffer.removeFirst())
            }
        }

    }

    override fun mapToMove(it: String): Move = with(Move.fromLine(it)) {
        return GroupMove(quantity, from, to)
    }

}


fun main() {
    val result = Puzzle5b().solveForFile()
    println("---")
    println(result)
}