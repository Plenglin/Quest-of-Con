package io.github.plenglin.util

class HeightMap(val grid: Array<Array<Double>>) {

    constructor(grid: Collection<Collection<Double>>): this(grid.map { it.toTypedArray() }.toTypedArray())

    val width = grid.size

    val normalized get(): HeightMap {
        val max = grid.flatten().max()!!
        val min = grid.flatten().min()!!
        println("max: $max, min: $min")
        return HeightMap(grid.map { col ->
            col.map { linMap(it, min, max, 0.0, 1.0) }.toTypedArray()
        }.toTypedArray())
    }

    operator fun times(other: HeightMap): HeightMap {
        return HeightMap((0 until width).map { i ->
            (0 until width).map { j ->
                this[i][j] * other[i][j]
            }
        })
    }

    operator fun times(other: Double): HeightMap {
        return HeightMap((0 until width).map { i ->
            (0 until width).map { j ->
                this[i][j] * other
            }
        })
    }

    operator fun plus(other: Double): HeightMap {
        return HeightMap((0 until width).map { i ->
            (0 until width).map { j ->
                this[i][j] + other
            }
        })
    }

    operator fun get(i: Int): Array<Double> {
        return grid[i]
    }

    /**
     * Interpolate the data.
     * @param x A number in [0, 1]
     * @param y A number in [0, 1]
     */
    operator fun get(x: Double, y: Double): Double {
        // Numbers and the cells
        val i = x * (width - 1)
        val j = y * (width - 1)
        val i1 = i.toInt()
        val j1 = j.toInt()
        val i2 = i1 + 1
        val j2 = j1 + 1

        // Get the sides
        val s1 = linMap(i, i1.toDouble(), i2.toDouble(), grid[i1][j1], grid[i2][j1])
        val s2 = linMap(i, i1.toDouble(), i2.toDouble(), grid[i1][j2], grid[i2][j1])

        // Interpolate the values at the sides
        return linMap(j, j1.toDouble(), j2.toDouble(), s1, s2)
    }

}