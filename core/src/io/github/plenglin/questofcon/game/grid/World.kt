package io.github.plenglin.questofcon.game.grid

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
        Array(height, { Tile(Terrains.grass) })
    })

    /**
     * Get the tile. If out of bounds, returns [null]
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

}

data class WorldCoords(val world: World, val i: Int, val j: Int) {

    val tile: Tile? = world[i, j]

    val exists: Boolean = world[i, j] != null

    /**
     * Perform a floodfill starting from this point.
     * @param radius how far from here to floodfill
     * @param set the set to add points to.
     * @param predicate should we include this in our floodfill?
     */
    fun floodfill(radius: Int, predicate: (WorldCoords) -> Boolean = { true }): MutableSet<WorldCoords> {

        var set = mutableSetOf<WorldCoords>(this)
        for (n in 1..radius) {
            println(n)
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

}

fun main(args: Array<String>) {
    val world = World(10, 10)
    var set = WorldCoords(world, 5, 5).floodfill(4)

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