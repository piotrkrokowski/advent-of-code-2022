import Puzzle18b.Content.*

const val MAX_DIMENSION = 20

// Cool tool: https://miabellaai.net/index.html
class Puzzle18b : Puzzle<Int> {

    data class Coords(
        val x: Int,
        val y: Int,
        val z: Int
    ) {

        fun getAdjacentCoords(): List<Coords> {
            return ADJACENT_DIRECTIONS.map { moveBy(it) }
        }

        private fun moveBy(move: Coords): Coords {
            return Coords(x + move.x, y + move.y, z + move.z)
        }

        companion object {
            val ADJACENT_DIRECTIONS: List<Coords> = listOf(
                Coords(-1, 0, 0),
                Coords(1, 0, 0),
                Coords(0, 1, 0),
                Coords(0, -1, 0),
                Coords(0, 0, -1),
                Coords(0, 0, 1),
            )
        }
    }

    enum class Content {
        UNEXPLORED, LAVA, WATER, AIR, EDGE;

        fun isExteriorSurface(): Boolean {
            if (!isExplored()) throw IllegalStateException()
            return this in setOf(WATER, EDGE)
        }

        fun isExplored(): Boolean {
            return this != UNEXPLORED
        }
    }

    class Exploration(private val area: Area) {
        private val inExploration: MutableSet<Coords> = mutableSetOf()
        private var onlyMetLava: Boolean = true

        // I'm so stubborn to use DSF... it's just more intuitive to me.
        fun explore(coords: Coords, topLevel: Boolean = true) {
            // println("Exploring ${coords}")
            inExploration.add(coords)
            val adjacentCoords = coords.getAdjacentCoords()
            for (adjacentCoord in adjacentCoords) {
                val adjacentContent = area[adjacentCoord]
                if (adjacentContent.isExplored()) {
                    if (adjacentContent in setOf(EDGE, WATER)) {
                        onlyMetLava = false
                        break
                    } else if (adjacentContent != LAVA) {
                        throw java.lang.IllegalStateException("Impossible state - bug")
                    }
                } else {
                    if (!inExploration.contains(adjacentCoord)) explore(adjacentCoord, false)
                    if (!onlyMetLava) break
                }
            }
            if (topLevel) {
                val result = if (onlyMetLava) AIR else WATER
                inExploration.forEach { area[it] = result }
                area[coords] = result
                /*if (onlyMetLava) {
                    println("Discovered ${result} bubble consisting of ${inExploration.size} items")
                }*/
            }
        }

    }

    class Area {
        val map: Array<Array<Array<Content>>> =
            Array(MAX_DIMENSION) { Array(MAX_DIMENSION) { Array(MAX_DIMENSION) { UNEXPLORED } } }
        private val lavaCubes: MutableSet<Coords> = mutableSetOf()

        fun getTotalNumberOfSidesNotTouchingAirOrLava(): Int {
            return lavaCubes.sumOf { getNumberOfSidesNotTouchingAirOrLava(it) }
        }

        fun addItemFromString(coordsString: String): Area {
            val split = coordsString.split(",")
            val coords = Coords(split[0].toInt(), split[1].toInt(), split[2].toInt())
            this[coords.x, coords.y, coords.z] = LAVA
            lavaCubes.add(coords)
            return this
        }

        private fun getNumberOfSidesNotTouchingAirOrLava(lavaCube: Coords): Int {
            return lavaCube
                .getAdjacentCoords()
                .map { getOrExplore(it).isExteriorSurface() }
                .count { it }
        }

        private fun getOrExplore(coords: Coords): Content {
            val currentContent = getAt(coords)
            if (currentContent.isExplored()) return currentContent
            val exploration = Exploration(this)
            exploration.explore(coords)
            return getAt(coords)
        }

        private fun getAt(coords: Coords) = this[coords.x, coords.y, coords.z]

        operator fun get(coords: Coords): Content {
            return getAt(coords)
        }

        private fun dimensionInBounds(d: Int): Boolean = d in 0 until MAX_DIMENSION

        private fun getAt(x: Int, y: Int, z: Int): Content {
            return if (arrayOf(x, y, z).all { dimensionInBounds(it) }) {
                map[x][y][z]
            } else {
                EDGE
            }
        }

        operator fun get(x: Int, y: Int, z: Int): Content {
            return getAt(x, y, z)
        }

        operator fun set(coords: Coords, content: Content) {
            map[coords.x][coords.y][coords.z] = content
        }

        operator fun set(x: Int, y: Int, z: Int, content: Content) {
            map[x][y][z] = content
        }

    }

    override fun solve(lines: List<String>): Int {
        val area = Area()
        lines.forEach { area.addItemFromString(it) }
        return area.getTotalNumberOfSidesNotTouchingAirOrLava()
    }

}

fun main() {
    val result = Puzzle18b().solveForFile()
    println("---")
    println(result)
}