package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.linMap
import io.github.plenglin.questofcon.logit
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

/**
 * Takes a biome height map and turns it into a [World].
 */
class MapToHeight(val world: World, val grid: HeightMap) {

    fun doHeightMap() {
        world.forEach {
            // Interpolate the values at the sides
            val h = grid[it.i.toDouble() / world.width, it.j.toDouble() / world.height]
            val tile = it.tile!!
            tile.elevation = (logit(h, 0.2) * Constants.ELEVATION_LEVELS).toInt()
        }
    }
}

class BiomeGenerator(val world: World, val height: HeightMap, val rainfall: HeightMap) {

    fun applyBiomes() {
        val waterDistribution = (rainfall * (height * -1.0 + 1.0)).normalized
        println(waterDistribution)
        world.forEach {
            // Interpolate the values at the sides
            val x = it.i.toDouble() / world.width
            val y = it.j.toDouble() / world.height

            val h = height[x, y]
            val tile = it.tile!!
            val water = waterDistribution[x, y]

            tile.biome = {
                if (tile.elevation == Constants.ELEVATION_LEVELS - 1) {
                    GameData.mountains
                }
                if (tile.elevation == 0) {
                    GameData.water
                }
                when {
                    tile.elevation == Constants.ELEVATION_LEVELS - 1 -> GameData.mountains
                    h > 0.7 -> GameData.highlands
                    h > 0.25 -> if (water > 0.5) GameData.grass else GameData.desert
                    else -> GameData.water
                }
            }()

        }

        val waterTiles = world.filter { it.tile!!.biome == GameData.water }
        val singleWaterTiles = waterTiles.filter { !it.surrounding().any { it.tile!!.biome == GameData.water} }
        singleWaterTiles.forEach { waterTile ->
            waterTile.tile!!.biome = waterTile.surrounding()[0].tile!!.biome
        }

        val beachTiles = waterTiles.map { it.surrounding().toSet() }.reduce { acc, list -> acc + list } - waterTiles
        beachTiles.forEach { it.tile!!.biome = GameData.beach }
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
