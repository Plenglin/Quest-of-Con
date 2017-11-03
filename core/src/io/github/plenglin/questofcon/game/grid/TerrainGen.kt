package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.linMap
import java.util.*


class DiamondSquareHeightGenerator(scale: Int, val initialOffsets: Double = 0.5, val iterativeRescale: Double = 0.5, seed: Long = 0) {

    val iterations = scale + 1
    val side = (2 shl scale)
    val length = side + 1
    val grid = Array<Array<Double>>(length, { Array(length, {0.0}) })
    val random = Random(seed)

    fun generate(): DiamondSquareHeightGenerator {
        // Initialize corners
        grid[0][0] = rand()
        grid[side][0] = rand()
        grid[0][side] = rand()
        grid[side][side] = rand()

        var offset = initialOffsets

        for (i in 1..iterations) {
            val hlen = side shr i
            println("i: $hlen")

            val affected = getSquaresAffected(i)
            val diamonds = getDiamonds(i)
            val squares = affected.subtract(diamonds)

            diamonds.forEach {
                diamond(it.first, it.second, hlen, offset * rand())
            }

            squares.forEach {
                square(it.first, it.second, hlen, offset * rand())
            }

            offset *= iterativeRescale
        }
        return this
    }

    fun rand(): Double {
        return 2 * random.nextDouble() - 1
    }

    fun getSquaresAffected(iteration: Int, total: Boolean = false): Set<Pair<Int, Int>> {
        val jump = side shr iteration
        val accumulated = (0..side step jump).map { i ->
            (0..side step jump).map { j ->
                Pair(i, j)
            }
        }.flatten().toSet()
        return if (total || iteration == 0) {
            accumulated
        } else {
            accumulated.subtract(getSquaresAffected(iteration - 1, true))
        }
    }

    fun getDiamonds(iteration: Int, total: Boolean = false): Set<Pair<Int, Int>> {
        val start = side shr (iteration)
        val jump = start * 2
        val accumulated = (start..side step jump).map { i ->
            (start..side step jump).map { j ->
                Pair(i, j)
            }
        }.flatten().toSet()
        //println("$iteration: $start..$side step $jump; $accumulated")
        return if (total || iteration == 1) {
            accumulated
        } else {
            accumulated.subtract(getDiamonds(iteration - 1, true))
        }
    }

    fun diamond(i: Int, j: Int, step_size: Int, offset: Double) {
        val avg = listOf(
                grid[i - step_size][j - step_size],
                grid[i + step_size][j - step_size],
                grid[i - step_size][j + step_size],
                grid[i + step_size][j + step_size]
        ).average()
        grid[i][j] = avg + offset
    }

    fun square(i: Int, j: Int, step_size: Int, offset: Double) {
        val avg = listOf(
                grid.getOrNull(i)?.getOrNull(j - step_size),
                grid.getOrNull(i + step_size)?.getOrNull(j),
                grid.getOrNull(i)?.getOrNull(j + step_size),
                grid.getOrNull(i - step_size)?.getOrNull(j)
        ).filterNotNull().average()
        grid[i][j] = avg + offset
    }

}

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

/**
 * Takes a terrain height map and turns it into a [World].
 */
class MapToHeight(val world: World, val grid: HeightMap) {

    fun doHeightMap() {
        world.forEach {
            // Interpolate the values at the sides
            val h = grid[it.i.toDouble() / world.width, it.j.toDouble() / world.height]
            //println("${it.i.toDouble() / world.width}")
            val tile = it.tile!!
            tile.terrain = when {
                h > 0.85 -> Terrains.mountains
                h > 0.75 -> Terrains.bigHills
                h > 0.65 -> Terrains.hills
                h > 0.25 -> Terrains.grass
                h > 0.15 -> Terrains.lowlands
                //h > 0.15 -> Terrains.desert
                else -> Terrains.water
            }
        }
    }
}

/**
 * Populate the world with vegetation. Assumes that lower areas get more water.
 */
class VegetationGenerator(val world: World, val height: HeightMap, val rainfall: HeightMap) {

}

fun main(args: Array<String>) {
    println("hello wurd")
    val g = DiamondSquareHeightGenerator(3, seed = 0)
    //println(g.getDiamonds(0))
    g.generate()
    val data = HeightMap(g.grid).normalized
    data.grid.forEach { col ->
        col.forEach {
            print("%.2f\t".format(it))
        }
        println()
    }
    println(data[0.0, 0.0])
}
