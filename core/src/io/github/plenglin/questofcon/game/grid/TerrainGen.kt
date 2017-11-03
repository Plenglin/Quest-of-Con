package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.linMap
import java.util.*


class DiamondSquareHeightGenerator(val scale: Int, val initialOffsets: Double = 0.5, val iterativeRescale: Double = 0.5, seed: Long = 0) {

    val iterations = scale + 1
    val side = (2 shl scale)
    val length = side + 1
    val grid = Array<Array<Double>>(length, { Array(length, {0.0}) })
    val random = Random(seed)

    fun generate() {
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
