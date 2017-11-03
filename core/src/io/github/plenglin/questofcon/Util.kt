package io.github.plenglin.questofcon


fun linMap(x: Double, a1: Double, b1: Double, a2: Double, b2: Double): Double {
    return (b2 - a2) * (x - a1) / (b1 - a1) + a2
}

fun linMap(x: Int, a1: Int, b1: Int, a2: Int, b2: Int): Int {
    return (b2 - a2) * (x - a1) / (b1 - a1) + a2
}

