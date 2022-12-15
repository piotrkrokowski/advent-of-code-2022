import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val MAGIC_NUMBER = 4000000

open class Puzzle15b(val maxCoordinate: Int = MAGIC_NUMBER) : Puzzle<Long> {

    data class Coords(val x: Int, val y: Int) {
        fun distanceTo(other: Coords): Int {
            return abs(x - other.x) + abs(y - other.y)
        }

        override fun toString(): String {
            return "(${x}, ${y})"
        }
    }

    data class Range(val from: Int, val to: Int) {
        fun isInRange(x: Int): Boolean {
            return x in from..to
        }

        fun intersectsWith(anotherRange: Range): Boolean {
            return anotherRange.from in from..to || anotherRange.to in from..to
        }

        fun combine(anotherRange: Range): Range? {
            return if (intersectsWith(anotherRange) || anotherRange.intersectsWith(this)) {
                Range(min(from, anotherRange.from), max(to, anotherRange.to))
            } else {
                null
            }
        }
    }

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
        val ranges: MutableList<Range> = mutableListOf()

        fun addRangeForSensor(sensor: Sensor) {
            sensor.rangeInLine(y)?.let { addRange(it) }
        }

        fun hasGaps(maxCoordinate: Int): Boolean {
            return if (ranges.size == 0)
                true
            else if (ranges.size == 1) {
                val onlyRange = ranges[0]
                onlyRange.from > 0 || onlyRange.to < maxCoordinate
            } else true
        }

        internal fun addRange(addedRange: Range) {
            var combinedRangeToAdd = addedRange
            val iterator = ranges.iterator()
            for (existingRange in iterator) {
                val combined = existingRange.combine(combinedRangeToAdd)
                if (combined != null) {
                    combinedRangeToAdd = combined
                    iterator.remove()
                }
            }
            ranges.add(combinedRangeToAdd)
        }

        fun getUncoveredPosition(maxCoordinate: Int): Coords? {
            for (x in 0..maxCoordinate) {
                if (!isRangeOfAnyScanner(x)) {
                    return Coords(x, y)
                }
            }
            return null
        }

        private fun isRangeOfAnyScanner(x: Int): Boolean {
            return ranges.any { it.isInRange(x) }
        }

    }

    override fun solve(lines: List<String>): Long {

        val analysedLines: Array<Line> = Array(maxCoordinate + 1) { Line(it) }
        val sensors: List<Sensor> = buildSensorsList(lines)

        for (line in analysedLines) {
            sensors.forEach { line.addRangeForSensor(it) }
            // println("Added ranges for line ${line.y}: ${line.ranges.size} : ${line.ranges}")
        }
        val filteredLines = analysedLines.filter { it.hasGaps(maxCoordinate) }
        println("Lines to process: ${filteredLines.size}")
        for (line in filteredLines) {
            println("Scanning line ${line.y}")
            val uncoveredPosition = line.getUncoveredPosition(maxCoordinate)
            if (uncoveredPosition != null) {
                return uncoveredPosition.x.toLong() * MAGIC_NUMBER + uncoveredPosition.y.toLong()
            }
        }
        throw IllegalStateException()
    }

    private fun buildSensorsList(lines: List<String>): List<Sensor> {
        val regex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
        return lines
            .map { regex.find(it) }
            .map {
                Sensor(
                    Coords(it!!.groupValues[1].toInt(), it.groupValues[2].toInt()),
                    Coords(it.groupValues[3].toInt(), it.groupValues[4].toInt())
                )
            }
    }
}

fun main() {
    val result = Puzzle15b().solveForFile()
    println("---")
    println(result)
}
