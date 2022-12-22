import Graph.Node

// Yeah, I've overinvested here. But it might come handy later :]
open class Graph<T : Node<T>> {

    val nodes: MutableMap<String, T> = mutableMapOf()
    val paths: MutableMap<NodePair<T>, Path> = mutableMapOf()

    inner class Path(val nodePair: NodePair<T>, val distance: Int, val proxy: T? = null) {
        fun getFullPathToTarget(): List<Node<T>> {
            val result = LinkedHashSet<Node<T>>()
            if (proxy == null) {
                result.add(nodePair.from)
                result.add(nodePair.to)
            } else {
                result.addAll(getPath(nodePair.from, proxy)!!.getFullPathToTarget())
                result.addAll(getPath(proxy, nodePair.to)!!.getFullPathToTarget())
            }
            return result.toList()
        }

        override fun toString(): String {
            return "<${nodePair.to.identifier} distance $distance thru ${proxy?.identifier}>"
        }


    }

    data class NodePair<T : Node<T>>(val from: T, val to: T) {

        override fun toString(): String {
            return "[${from.identifier} -> ${to.identifier}]"
        }

//        companion object {
//            operator fun invoke(from: Node, to: Node): NodePair {
//                val lower = if (from <= to) from else to
//                val greater = if (from > to) from else to
//                return NodePair(lower, greater)
//            }
//        }


    }

    data class OrderedPair<T : Node<T>> private constructor(val from: T, val to: T) {

        override fun toString(): String {
            return "[${from.identifier} -> ${to.identifier}]"
        }

        companion object {
            operator fun <T : Node<T>> invoke(from: T?, to: T?): OrderedPair<T> {
                if (from == null) return OrderedPair(to, null)
                else if (to == null) return OrderedPair(from, null) else {
                    val lower = if (from <= to) from else to
                    val greater = if (from > to) from else to
                    return OrderedPair(lower, greater)
                }
            }
        }


    }

    data class Connection<T : Node<T>> constructor(val to: T, var distance: Int)

    open class Node<T : Node<T>>(
        val identifier: String,
    ) : Comparable<T> {

        val connections: MutableSet<Connection<T>> = mutableSetOf()

        fun addConnection(node: T, distance: Int) {
            connections.add(Connection(node, distance))
            node.connections.add(Connection(this as T, distance))
        }

        override fun compareTo(other: T): Int {
            return identifier.compareTo(other.identifier)
        }

        override fun toString(): String {
            return "($identifier)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Node<*>) return false

            if (identifier != other.identifier) return false

            return true
        }

        override fun hashCode(): Int {
            return identifier.hashCode()
        }


    }

    fun addNode(node: T) {
        nodes[node.identifier] = node
    }

    fun getNode(identifier: String): T? {
        return nodes[identifier]
    }

    fun addConnection(from: String, to: String, distance: Int = 1) {
        getNode(from)!!.addConnection(getNode(to)!!, distance)
    }

    fun getPath(from: String, to: String): Path? {
        val nodeFrom = getNode(from)
        val nodeTo = getNode(to)
        if (nodeFrom != null && nodeTo != null)
            return paths[NodePair<T>(nodeFrom, nodeTo)]
        else
            return null
    }

    fun getAllPathsFrom(identifier: String): List<Path> {
        val fromNode = getNode(identifier)
        return paths.filterKeys { it.from == fromNode }.values.toList()
    }

    operator fun get(identifier: String): T? {
        return getNode(identifier)
    }

    operator fun get(from: String, to: String): Path? {
        return getPath(from, to)
    }

    private fun getPath(from: T, to: T): Path? {
        return paths[NodePair(from, to)]
    }

    private fun setKnownShortestDistance(from: T, to: T, path: Path) {
        paths[NodePair(from, to)] = path
    }

    // My personal implementation of Floyd–Warshall algorithm... Yeah, finally I'm learning something
    fun calculateDistances() {
        nodes.values.forEach { paths[NodePair(it, it)] = Path(NodePair(it, it), 0) }
        nodes.values.forEach { node ->
            node.connections.forEach { connection ->
                paths[NodePair(node, connection.to)] =
                    Path(NodePair(node, connection.to), connection.distance)
            }
        }
        for (proxy in nodes.values) {
            for (from in nodes.values) {
                for (to in nodes.values) {
                    val knownPathToTarget = getPath(from, to)
                    val pathToProxy = getPath(from, proxy)
                    val pathFromProxy = getPath(proxy, to)
                    if (pathToProxy != null && pathFromProxy != null) {
                        val pathViaProxy = pathToProxy.distance + pathFromProxy.distance
                        if (knownPathToTarget == null || knownPathToTarget.distance > pathViaProxy) {
                            setKnownShortestDistance(
                                from, to, Path(NodePair(from, to), pathViaProxy, proxy)
                            )
                        }
                    }
                }
            }
        }
    }

}
