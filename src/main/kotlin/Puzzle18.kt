class Puzzle18 : Puzzle<Int> {

    data class Coords3d(
        val x: Int,
        val y: Int,
        val z: Int
    ) {

        fun getAdjacentCoords(): List<Coords3d> {
            return ADJACENT_DIRECTIONS.map { moveBy(it) }
        }

        fun moveBy(move: Coords3d): Coords3d {
            return Coords3d(x + move.x, y + move.y, z + move.z)
        }

        fun countNotConnectedSides(area: Area): Int {
            return getAdjacentCoords().map { area.getAt(it) }.count { connected -> !connected }
        }

        companion object {
            val ADJACENT_DIRECTIONS: List<Coords3d> = listOf(
                Coords3d(-1, 0, 0),
                Coords3d(1, 0, 0),
                Coords3d(0, 1, 0),
                Coords3d(0, -1, 0),
                Coords3d(0, 0, -1),
                Coords3d(0, 0, 1),
            )
        }
    }

    class Area {
        val items: MutableSet<Coords3d> = mutableSetOf()

        fun getTotalNumberOfNotConnectedSides(): Int {
            return items.map { it.countNotConnectedSides(this) }.sum()
        }

        fun addItemFromString(coords: String): Area {
            val split = coords.split(",")
            items.add(Coords3d(split[0].toInt(), split[1].toInt(), split[2].toInt()))
            return this
        }

        fun getAt(coords3d: Coords3d): Boolean {
            return items.contains(coords3d)
        }
    }

    override fun solve(lines: List<String>): Int {
        val area = Area()
        lines.forEach { area.addItemFromString(it) }
        return area.getTotalNumberOfNotConnectedSides()
    }
}

fun main() {
    val result = Puzzle18().solveForFile()
    println("---")
    println(result)
}