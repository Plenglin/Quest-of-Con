package io.github.plenglin.questofcon


fun linMap(x: Double, a1: Double, b1: Double, a2: Double, b2: Double): Double {
    return (b2 - a2) * (x - a1) / (b1 - a1) + a2
}

fun logit(x: Double, b: Double, a: Double): Double {
    val x1 = ((x - 0.5) / a) + 0.5
    return b * Math.log(x1 / (1 - x1)) + 0.5
}

fun logit(x: Double, b: Double): Double {
    val exp = Math.exp(0.5 / b)
    val a = (1 + exp) / (exp - 1)
    return logit(x, b, a)
}

class ListenerManager<T> {
    private val listeners = mutableMapOf<Long, (T) -> Unit>()
    private var nextListenerId = 0L

    fun addListener(listener: (T) -> Unit): Long {
        val id = nextListenerId++
        listeners.put(id, listener)
        return id
    }

    fun fire(data: T) {
        listeners.forEach { _, l -> l(data) }
    }

    fun removeListener(id: Long) {
        listeners.remove(id)
    }

}

class ObjectRegistry<T> : Iterable<T> where T : Registerable {

    private val objects = mutableMapOf<Long, T>()
    private val idToName = mutableMapOf<Long, String>()
    private val nameToId = mutableMapOf<String, Long>()

    private var nextLongId = 0L

    fun register(obj: T): Long {
        assert(!obj.name.contains(' '), { "Object name cannot contain spaces!" })
        val id = newId(obj.name)
        objects.put(id, obj)
        return id
    }

    private fun newId(name: String): Long {
        val id = nextLongId++;
        idToName.put(id, name)
        nameToId.put(name, id)
        return id
    }

    operator fun get(name: String): T {
        val id = nameToId[name]!!
        return objects[id]!!
    }

    operator fun get(id: Long): T {
        return objects[id]!!
    }

    override fun iterator(): Iterator<T> {
        return objects.values.iterator()
    }

}

interface Registerable {
    var id: Long
    val name: String
}