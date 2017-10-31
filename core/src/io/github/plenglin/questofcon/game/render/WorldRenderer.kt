package io.github.plenglin.questofcon.game.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.plenglin.questofcon.game.grid.World


class WorldRenderer(val world: World) {

    val shape: ShapeRenderer = ShapeRenderer()

    fun render(drawGrid: Boolean = false) {
        shape.begin(ShapeRenderer.ShapeType.Filled)

        world.apply {
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

}