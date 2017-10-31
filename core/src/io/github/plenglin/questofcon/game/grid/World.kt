package io.github.plenglin.questofcon.game.grid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/**
 *
 */
class World(val width: Int, val height: Int) {

    val grid = Array(width, {
        Array(height, { Tile(Terrains.grass) })
    })

    operator fun get(i: Int, j: Int): Tile {
        return grid[i][j]
    }

}