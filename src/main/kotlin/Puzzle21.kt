class Puzzle21 : Puzzle<Long> {

    sealed class Register(val name: String) {
        abstract fun getValue(registry: Registry): Long

        companion object {
            private val REGEX = Regex("([a-z]{4}) ([+\\-*\\/]) ([a-z]{4})")
            fun fromString(string: String): Register {
                val split = string.split(":")
                val name = split[0]
                val const = split[1].trim().toLongOrNull()
                if (const != null) {
                    return Constant(name, const)
                } else {
                    val found = REGEX.find(split[1])
                    with(found!!) {
                        val operationType = OperationType.formChar(groupValues[2][0])
                        return Operation(name, operationType, groupValues[1], groupValues[3])
                    }
                }
            }
        }
    }

    class Registry(val registry: Map<String, Register>) {
        fun get(name: String): Register {
            return registry[name]!!
        }
    }

    class Constant(name: String, val value: Long) : Register(name) {
        override fun getValue(registry: Registry): Long {
            return value
        }
    }

    enum class OperationType(val function: (a: Long, b: Long) -> Long, val char: Char) {
        ADD({ a, b -> a + b }, '+'),
        SUBTRACT({ a, b -> a - b }, '-'),
        MULTIPLY({ a, b -> a * b }, '*'),
        DIVIDE({ a, b -> a / b }, '/');

        companion object {
            fun formChar(char: Char): OperationType {
                return values().first { it.char == char }
            }
        }
    }

    class Operation(
        name: String,
        val type: OperationType,
        val leftArgument: String,
        val rightArgument: String
    ) : Register(name) {
        override fun getValue(registry: Registry): Long {
            val left = registry.get(leftArgument).getValue(registry)
            val right = registry.get(rightArgument).getValue(registry)
            val value = type.function.invoke(left, right)
            // println(value)
            return value
        }
    }

    override fun solve(lines: List<String>): Long {
        val registers: Map<String, Register> = lines.map { Register.fromString(it) }
            .groupingBy { it.name }
            .reduce { _, _, element -> element }
        val registry = Registry(registers)
        val root = registry.get("root")
        return root.getValue(registry)
    }
}

fun main() {
    val result = Puzzle21().solveForFile()
    println("---")
    println(result)
}