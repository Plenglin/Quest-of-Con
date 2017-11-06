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
