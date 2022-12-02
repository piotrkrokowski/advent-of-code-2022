import java.io.File

interface Puzzle<T> {
    fun solve(lines: List<String>): T
    fun solveForFile(): T {
        var baseFileName = this::class.simpleName?.lowercase() ?: throw IllegalStateException()
        if (baseFileName.endsWith("b"))
            baseFileName = baseFileName.substringBeforeLast("b")

        return solve(File("src/main/resources/$baseFileName.txt").readLines())
    }
}