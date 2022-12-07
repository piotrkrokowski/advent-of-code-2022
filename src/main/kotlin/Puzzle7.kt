private const val SEARCHED_DIR_MAX_SIZE = 100000

open class Puzzle7 : Puzzle<Int> {

    sealed class Node(val name: String, val parent: Directory?)

    class Directory(
        name: String, parent: Directory?,
        private val contents: MutableList<Node> = mutableListOf()
    ) : Node(name, parent) {
        var cachedSize: Int = 0
        fun add(file: File) {
            cachedSize += file.ownSize
            contents.add(file)
        }

        fun add(directory: Directory) {
            cachedSize += directory.cachedSize
            contents.add(directory)
        }

        fun getSubdirectories(): List<Directory> {
            val onlySubdirectories = contents.filterIsInstance<Directory>()
            val subdirectoriesFlattened = onlySubdirectories.flatMap { it.getSubdirectories() }
            return onlySubdirectories + subdirectoriesFlattened
        }

        override fun toString(): String {
            return "Directory $name, cachedSize=$cachedSize"
        }

    }

    class File(name: String, parent: Directory?, val ownSize: Int) : Node(name, parent) {
        override fun toString(): String {
            return "File $name"
        }
    }

    // My bet was that in part 2 it will be enough to change the selector or sth... Not this time :(
    // This makes Selector concept useless in Puzzle7b and introduces "legacy" code. Lesson learnt, I guess.
    class Selector(
        private val markedDirectories: MutableList<Directory> = mutableListOf(),
        var totalSize: Int = 0
    ) {
        fun add(directory: Directory) {
            markedDirectories.add(directory)
            totalSize += directory.cachedSize
        }

        override fun toString(): String {
            return "Selector(markedDirectories=$markedDirectories, totalSize=$totalSize)"
        }

    }

    data class Navigation(var currentDir: Directory) {
        fun goUp() {
            currentDir = currentDir.parent!!
        }
    }

    private fun handleCdCommands(line: String, navigation: Navigation, selector: Selector): Boolean {
        if (line.startsWith("$ cd")) {
            if (line == "$ cd ..") {
                val closedDirectory = navigation.currentDir
                if (closedDirectory.cachedSize <= SEARCHED_DIR_MAX_SIZE) {
                    selector.add(navigation.currentDir)
                }
                navigation.goUp()
                navigation.currentDir.add(closedDirectory)
            } else {
                val newDirectory = Directory(line.substring(5), navigation.currentDir)
                navigation.currentDir = newDirectory
            }
            return true
        }
        return false
    }

    private fun handleFiles(line: String, navigation: Navigation): Boolean {
        if (line[0].isDigit()) {
            val split = line.split(" ")
            val newFile = File(split[1], navigation.currentDir, split[0].toInt())
            navigation.currentDir.add(newFile)
            return true
        }
        return false
    }

    // Nah, to hell with statelessness :P
    protected val root = Directory("/", null)
    private val navigation = Navigation(root)
    private val selector = Selector()

    private fun goBackToRootDir() {
        while (navigation.currentDir != root) {
            handleCdCommands("$ cd ..", navigation, selector)
        }
    }

    private fun processCommands(lines: List<String>) {
        for (line in lines) {
            handleCdCommands(line, navigation, selector)
            handleFiles(line, navigation)
        }
        // Nah, didn't perceive that at the end there won't be cd .., which "closes" the dir for me...
        // Need to compensate that
        goBackToRootDir()
    }

    override fun solve(lines: List<String>): Int {
        processCommands(lines.drop(1)) // In my solution I create root dir on my own, so I want to skip first cd /
        return selector.totalSize
    }

}

fun main() {
    val result = Puzzle7().solveForFile()
    println("---")
    println(result)
}

