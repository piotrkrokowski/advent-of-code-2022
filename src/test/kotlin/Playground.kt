import org.fest.assertions.Assertions.*
import org.junit.jupiter.api.Test

class Playground {

    data class Container(val nullableValue:String?)
    @Test
    fun nulls() {
        val s:String? = null
        val container:Container? = null
        println(s?:"is null") // is null
        println(container?.nullableValue) // null
        println(container?.nullableValue ?: "IS NULL") // IS NULL
        val intValue:Int? = s?.toInt() // null
        println(intValue)
    }

    @Test
    fun qualifiedThis() {
        fun String.wrapWithSquareBrackets():String = "[${this}]"
        val func: (String) -> String = { it ->
            var tmp = it
            //tmp = "[${tmp}]"
            tmp = tmp.wrapWithSquareBrackets()
            tmp
        }

        val result = func.invoke("ll")

        assertThat(result).isEqualTo("[ll]")
    }
}