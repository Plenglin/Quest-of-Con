package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.net.DataPosition

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

    constructor(world: World, pos: DataPosition) : this(world, pos.i, pos.j)

}