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

    fun draw(shape: ShapeRenderer, drawGrid: Boolean = true) {

        shape.begin(ShapeRenderer.ShapeType.Filled)

        // Draw the terrain
        grid.forEachIndexed { i, col ->
            col.forEachIndexed { j, tile ->
                shape.color = tile.terrain.color
                shape.rect(i.toFloat(), j.toFloat(), 1f, 1f)
            }
        }
        shape.end()

        // Draw the grid if necessary
        if (drawGrid) {
            shape.color = Color(0f, 0.5f, 1f, 0.5f)
            shape.begin(ShapeRenderer.ShapeType.Line)
            // Columns
            for (i in 0..width) {
                val x = i.toFloat()
                shape.line(x, 0f, x, height.toFloat())
            }

            // Rows
            for (j in 0..height) {
                val y = j.toFloat()
                shape.line(0f, y, width.toFloat(), y)
            }
            shape.end()
        }

    }

}