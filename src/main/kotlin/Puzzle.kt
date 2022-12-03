import java.io.File

interface Puzzle<T> {
    fun solve(lines: List<String>): T
    fun solveForFile(): T {
        return solveForFile({ s -> s })
    }

    fun solveForFile(pathModifier: (String) -> String): T {
        var path = this::class.simpleName?.lowercase() ?: throw IllegalStateException()
        if (path.endsWith("b"))
            path = path.substringBeforeLast("b")
        path = "src/main/resources/$path.txt"
        path = pathModifier.invoke(path)
        return solve(File(path).readLines())
    }

}