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

    fun normalized(): Array<Array<Double>> {
        val max = grid.flatten().max()!!
        val min = grid.flatten().min()!!
        return grid.map { col ->
            col.map { linMap(it, min, max, 0.0, 1.0) }.toTypedArray()
        }.toTypedArray()
    }

}

/**
 * Takes a terrain height map and turns it into a [World].
 */
class MapToHeight(val world: World, val grid: Array<Array<Double>>) {

    val width = world.width

    fun doHeightMap() {
        val data = world.forEach {
            // Numbers and the cells
            val x = it.i.toDouble() * (grid.size - 1) / width
            val y = it.j.toDouble() * (grid.size - 1) / width
            val i1 = x.toInt()
            val j1 = y.toInt()
            val i2 = i1 + 1
            val j2 = j1 + 1

            // Get the sides
            val s1 = linMap(x, i1.toDouble(), i2.toDouble(), grid[i1][j1], grid[i2][j1])
            val s2 = linMap(x, i1.toDouble(), i2.toDouble(), grid[i1][j2], grid[i2][j1])

            // Interpolate the values at the sides
            val h = linMap(y, j1.toDouble(), j2.toDouble(), s1, s2)

            val tile = it.tile!!
            tile.terrain = when {
                h > 0.85 -> Terrains.mountains
                h > 0.65 -> Terrains.hills
                h > 0.25 -> Terrains.grass
                h > 0.15 -> Terrains.lowlands
                //h > 0.15 -> Terrains.desert
                else -> Terrains.water
            }
        }
    }
}

fun main(args: Array<String>) {
    println("hello wurd")
    val g = DiamondSquareHeightGenerator(3, seed = 0)
    //println(g.getDiamonds(0))
    g.generate()
    g.normalized().forEach { col ->
        col.forEach {
            print("%.2f\t".format(it))
        }
        println()
    }
}
