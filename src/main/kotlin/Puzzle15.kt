import kotlin.math.abs

open class Puzzle15(val lineIndex: Int) : Puzzle<Int> {

    data class Coords(val x: Int, val y: Int) {
        fun distanceTo(other: Coords): Int {
            return abs(x - other.x) + abs(y - other.y)
        }

        override fun toString(): String {
            return "(${x}, ${y})"
        }
    }

    data class Range(val from: Int, val to: Int)

    data class Sensor(val coords: Coords, val beaconCoords: Coords) {
        fun rangeInLine(y: Int): Range? {
            val remainingRange = range() - abs(coords.y - y)
            if (remainingRange > 0)
                return Range(coords.x - remainingRange, coords.x + remainingRange)
            else
                return null
        }

        private fun range(): Int {
            return coords.distanceTo(beaconCoords)
        }
    }

    class Line(val y: Int) {
        internal val coordsWithinAlRanges: MutableSet<Int> = mutableSetOf()
        internal val existingBeacons: MutableSet<Int> = mutableSetOf()

        fun getNumberOfPositionsWithoutBeacon(): Int {
            return (coordsWithinAlRanges - existingBeacons).size
        }

        fun addRangeForSensor(sensor: Sensor) {
            if (sensor.beaconCoords.y == y) {
                existingBeacons.add(sensor.beaconCoords.x)
            }
            sensor.rangeInLine(y)?.let { addRange(it) }
        }

        private fun addRange(range: Range) {
            for (x in range.from..range.to) {
                coordsWithinAlRanges.add(x)
            }
        }

    }

    override fun solve(lines: List<String>): Int {
        val regex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
        val line = Line(lineIndex)
        lines
            .map { regex.find(it) }
            .map {
                Sensor(
                    Coords(it!!.groupValues[1].toInt(), it.groupValues[2].toInt()),
                    Coords(it.groupValues[3].toInt(), it.groupValues[4].toInt())
                )
            }
            .forEach {
                line.addRangeForSensor(it)
            }
        // println(line.coordsWithinAlRanges)
        // println(line.existingBeacons)
        return line.getNumberOfPositionsWithoutBeacon()
    }
}

fun main() {
    val result = Puzzle15(2000000).solveForFile()
    println("---")
    println(result)
}