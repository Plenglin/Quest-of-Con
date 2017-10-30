package io.github.plenglin.questofcon.game.grid

/**
 *
 */
class World(val width: Int, val length: Int) {

    val grid = Array(width, {
        Array(length, { Tile(Terrains.grass) })
    })

    operator fun get(i: Int, j: Int): Tile {
        return grid[i][j]
    }

}
