package io.github.plenglin.questofcon.game.grid

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
