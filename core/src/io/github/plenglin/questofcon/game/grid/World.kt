package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.net.DataPosition
import io.github.plenglin.questofcon.net.DataWorldState

/**
 *
 */
class World(val width: Int, val height: Int) : Sequence<WorldCoords> {
    override fun iterator(): Iterator<WorldCoords> {
        return grid.mapIndexed { i, arr ->
            arr.mapIndexed { j, tile ->
                WorldCoords(this@World, i, j)
            }
        }.flatten().iterator()
    }

    val grid = Array(width, {
        Array(height, { Tile() })
    })

    /**
     * Get the tile. If out of bounds, returns null
     */
    operator fun get(i: Int, j: Int): Tile? {
        try {
            return grid[i][j]
        } catch (e: ArrayIndexOutOfBoundsException) {
            return null
        }
    }

    fun contains(i: Int, j: Int): Boolean {
        return this[i, j] != null
    }

    operator fun contains(coords: WorldCoords): Boolean {
        return coords.world == this && contains(coords.i, coords.j)
    }

    fun serialized(): DataWorldState {
        return DataWorldState(
                grid.map { col ->
                    col.map { it.serialized() }.toTypedArray()
                }.toTypedArray()
        )
    }

}

data class WorldCoords(val world: World, val i: Int, val j: Int) {

    val tile: Tile? = world[i, j]

    val exists: Boolean = world[i, j] != null

    /**
     * Perform a floodfill starting from this point.
     * @param radius how far from here to floodfill
     * @param predicate should we include this in our floodfill?
     */
    fun floodfill(radius: Int, predicate: (WorldCoords) -> Boolean = { true }): MutableSet<WorldCoords> {

        var set = mutableSetOf<WorldCoords>(this)
        for (n in 1..radius) {
            val newSet = mutableSetOf<WorldCoords>()
            set.forEach { coord ->
                listOf(
                        WorldCoords(world, coord.i + 1, coord.j),
                        WorldCoords(world, coord.i - 1, coord.j),
                        WorldCoords(world, coord.i, coord.j + 1),
                        WorldCoords(world, coord.i, coord.j - 1)
                ).forEach { surr ->
                    if (surr in world && !set.contains(surr) && predicate(surr)) {
                        newSet.add(surr)
                    }
                }
            }
            set = set.union(newSet).toMutableSet()
        }

        return set
    }

    fun surrounding(includeNonexistent: Boolean = false): List<WorldCoords> {
        return listOf(
                WorldCoords(world, i + 1, j),
                WorldCoords(world, i, j + 1),
                WorldCoords(world, i - 1, j),
                WorldCoords(world, i, j - 1)
        ).filter { includeNonexistent || it.exists }
    }

    fun serialized(): DataPosition = DataPosition(i, j)

}

fun main(args: Array<String>) {
    val world = World(10, 10)
    val set = WorldCoords(world, 5, 5).floodfill(4)

    for (i in 0 until 10) {
        for (j in 0 until 10) {
            if (WorldCoords(world, i, j) in set) {
                print(" x ")
            } else {
                print(" o ")
            }
        }
        println()
    }
}