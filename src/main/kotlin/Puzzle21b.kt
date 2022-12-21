import Puzzle21b.OperationType.*

private const val HUMN = "humn"

@Suppress("DuplicatedCode")
class Puzzle21b : Puzzle<Long> {

    interface HasEquationDependency {
        var equationDependency: Operation?
    }

    sealed class Register(val name: String) {
        abstract fun evaluate(registry: Registry): Long?

        companion object {
            private val REGEX = Regex("([a-z]{4}) ([+\\-*/]) ([a-z]{4})")
            fun fromString(string: String): Register {
                val split = string.split(":")
                val name = split[0]
                val const = split[1].trim().toLongOrNull()
                if (name == HUMN) {
                    return Human()
                } else if (const != null) {
                    return Constant(name, const)
                } else {
                    val found = REGEX.find(split[1])
                    with(found!!) {
                        val operationType = if (name == "root") EQUALS else OperationType.formChar(groupValues[2][0])
                        return Operation(name, operationType, groupValues[1], groupValues[3])
                    }
                }
            }
        }
    }

    class Registry(private val registry: Map<String, Register>) {
        fun get(name: String): Register {
            return registry[name]!!
        }
    }

    class Constant(name: String, val value: Long) : Register(name) {
        override fun evaluate(registry: Registry): Long {
            return value
        }
    }

    enum class OperationType(val function: (a: Long, b: Long) -> Long, val char: Char) {
        ADD({ a, b -> a + b }, '+'),
        SUBTRACT({ a, b -> a - b }, '-'),
        MULTIPLY({ a, b -> a * b }, '*'),
        DIVIDE({ a, b -> a / b }, '/'),
        EQUALS({ _, _ -> throw UnsupportedOperationException() }, '='); // Dirty, but I don't have time right now;]

        fun opposite(): OperationType {
            return when (this) {
                ADD -> SUBTRACT
                SUBTRACT -> ADD
                MULTIPLY -> DIVIDE
                DIVIDE -> MULTIPLY
                EQUALS -> EQUALS
            }
        }

        fun isAlternating(): Boolean {
            return this in listOf(ADD, MULTIPLY)
        }

        companion object {
            fun formChar(char: Char): OperationType {
                return values().first { it.char == char }
            }
        }
    }

    class Human : Register(HUMN), HasEquationDependency {
        override var equationDependency: Operation? = null

        override fun evaluate(registry: Registry): Long? {
            return null
        }

        fun solve(): Long {
            return equationDependency!!.solveEquation()
        }
    }

    open class Operation(
        name: String,
        private val type: OperationType,
        private val leftArgument: String,
        private val rightArgument: String,
    ) : Register(name), HasEquationDependency {
        private var evaluatedLeftArgument: Long? = null
        private var evaluatedRightArgument: Long? = null
        private var evaluatedValue: Long? = null // for debugging only
        override var equationDependency: Operation? = null

        override fun evaluate(registry: Registry): Long? {
            val leftRegister = registry.get(leftArgument)
            val rightRegister = registry.get(rightArgument)
            evaluatedLeftArgument = leftRegister.evaluate(registry)
            evaluatedRightArgument = rightRegister.evaluate(registry)
            if (evaluatedLeftArgument != null && evaluatedRightArgument != null) {
                evaluatedValue = type.function.invoke(evaluatedLeftArgument!!, evaluatedRightArgument!!)
                return evaluatedValue
            } else {
                if (evaluatedLeftArgument == null) {
                    (leftRegister as HasEquationDependency).equationDependency = this
                }
                if (evaluatedRightArgument == null) {
                    (rightRegister as HasEquationDependency).equationDependency = this
                }
                return null
            }
        }

        fun solveEquation(): Long {
            if (type == EQUALS) {
                return if (evaluatedLeftArgument != null) evaluatedLeftArgument!! else evaluatedRightArgument!!
            }
            if (equationDependency == null) {
                throw IllegalStateException(this.toString())
            }
            val equationResult = equationDependency!!.solveEquation()
            val anyKnownArgument =
                if (evaluatedLeftArgument != null) evaluatedLeftArgument!! else evaluatedRightArgument!!
            if (type.isAlternating()) {
                return type.opposite().function.invoke(equationResult, anyKnownArgument)
            } else {
                val operation = if (evaluatedRightArgument == null) type else type.opposite()
                return operation.function.invoke(anyKnownArgument, equationResult)
            }
        }

        override fun toString(): String {
            return "Operation $name: $leftArgument ${type.char} $rightArgument. evaluatedLhs=$evaluatedLeftArgument, evaluatedRhs=$evaluatedRightArgument. equationDependency=$equationDependency"
        }


    }

    override fun solve(lines: List<String>): Long {
        val registers: Map<String, Register> = lines.map { Register.fromString(it) }
            .groupingBy { it.name }
            .reduce { _, _, element -> element }
        val registry = Registry(registers)
        val root = registry.get("root")
        root.evaluate(registry)
        return (registry.get(HUMN) as Human).solve()
    }
}

fun main() {
    val result = Puzzle21b().solveForFile()
    println("---")
    println(result)
}