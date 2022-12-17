import Graph.Node
import org.fest.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test

class GraphTest {

    class SimpleNode(identifier: String) : Node<SimpleNode>(identifier)

    @Test
    fun shouldCalculateDistances() {
        // given
        val graph = Graph<SimpleNode>()
        ('A'..'H')
            .map { SimpleNode(it.toString()) }
            .forEach { graph.addNode(it) }
        graph.addConnection("A", "B")
        graph.addConnection("A", "C")
        graph.addConnection("B", "C")
        graph.addConnection("B", "D")
        graph.addConnection("B", "E")
        graph.addConnection("E", "F")
        graph.addConnection("A", "G")
        graph.addConnection("G", "H")
        graph.addConnection("E", "H")

        // when
        graph.calculateDistances()

        // then

        // zeros
        assertThat(graph["A", "A"]!!.distance).isEqualTo(0)

        // simple pats
        assertThat(graph["A", "B"]!!.distance).isEqualTo(1)
        assertThat(graph["A", "C"]!!.distance).isEqualTo(1)
        assertThat(graph["A", "D"]!!.distance).isEqualTo(2)
        assertThat(graph["A", "E"]!!.distance).isEqualTo(2)
        assertThat(graph["A", "F"]!!.distance).isEqualTo(3)
        assertThat(graph["A", "G"]!!.distance).isEqualTo(1)
        assertThat(graph["A", "H"]!!.distance).isEqualTo(2)

        // reverse path
        assertThat(graph["H", "A"]!!.distance).isEqualTo(2)

        // multi-path choices
        assertThat(graph["B", "H"]!!.distance).isEqualTo(2)
        assertThat(graph["B", "H"]!!.getFullPathToTarget()).containsExactly(graph["B"], graph["E"], graph["H"])

//        println(graph.paths.values.joinToString("\n"))
//        println(graph.getPath("C", "E")!!.getFullPathToTarget())

    }
}