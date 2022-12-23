import java.io.File

interface Puzzle<T> {
    fun solve(lines: List<String>): T
    fun solveForFile(): T {
        return solveForFile { s -> s }
    }

    fun solveForFile(pathModifier: (String) -> String): T {
        val path = getPath(pathModifier)
        return solve(File(path).bufferedReader().readLines())
    }

    fun getPath(pathModifier: (String) -> String): String {
        var path = this::class.simpleName?.lowercase() ?: throw IllegalStateException()
        if (path.endsWith("b"))
            path = path.substringBeforeLast("b")
        path = "src/main/resources/$path.txt"
        path = pathModifier.invoke(path)
        return path
    }
}