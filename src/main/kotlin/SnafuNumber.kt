import java.math.BigInteger

class SnafuNumber(val value: String) {

    fun toDecimal(): BigInteger {
        var power = BigInteger.ONE
        var result = BigInteger.ZERO
        for (stringPosition in value.indices.reversed()) {
            val digitAsDecimal = snafuDigitToDecimal(value[stringPosition]).toLong()
            val multiplied = BigInteger.valueOf(digitAsDecimal).multiply(power)
            result = result.plus(multiplied)
            power = power.multiply(BASE)
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SnafuNumber
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value
    }


    companion object {

        val BASE: BigInteger = BigInteger.valueOf(5L)
        private val CONVERSION_TABLE: CharArray = "012=-".toCharArray()

        fun fromDecimal(decimal: BigInteger): SnafuNumber {
            var remainingValue = decimal
            val digits: MutableList<Char> = ArrayList()

            while (remainingValue.compareTo(BigInteger.ZERO) > 0) {
                val divideAndRemainder = remainingValue.divideAndRemainder(BASE)
                val remainder = divideAndRemainder[1].toInt()
                val extraOverflow = if (remainder > 2) BigInteger.ONE else BigInteger.ZERO
                remainingValue = divideAndRemainder[0].plus(extraOverflow)
                val digit: Char = CONVERSION_TABLE[remainder]
                digits.add(digit)
            }

            return SnafuNumber(String(digits.reversed().toCharArray()))
        }

        fun snafuDigitToDecimal(digit: Char): Int {
            return when (digit) {
                '=' -> -2
                '-' -> -1
                '0', '1', '2' -> digit.digitToInt()
                else -> throw IllegalArgumentException(digit.toString())
            }
        }

        /*fun decimalDigitToSnafu(digit: Int):Char {
            return when (digit) {
                -2 -> '='
                -1 -> '-'
                0, 1, 2 -> digit.digitToChar()
                else -> throw IllegalArgumentException(digit.toString())
            }
        }*/
    }


}