package io.github.plenglin.questofcon.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import ktx.app.color


class WorldRenderer(val world: World) {

    val shape: ShapeRenderer = ShapeRenderer()

    fun render(drawGrid: Boolean = true, vararg paints: SelectionSet) {

        shape.setAutoShapeType(true)
        shape.begin()

        world.apply {
            // Draw the terrain
            shape.set(ShapeRenderer.ShapeType.Filled)
            grid.forEachIndexed { i, col ->
                col.forEachIndexed { j, tile ->
                    shape.color = tile.terrain.color
                    shape.rect(i.toFloat(), j.toFloat(), 1f, 1f)
                }
            }

            // Draw the grid if necessary
            if (drawGrid) {
                shape.color = Color(0f, 0.5f, 1f, 0.5f)
                shape.set(ShapeRenderer.ShapeType.Line)
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
            }

            // Fill in the selection sets
            shape.set(ShapeRenderer.ShapeType.Filled)
            for (s in paints) {
                s.coords.forEach {
                    shape.color = s.color
                    shape.rect(it.i.toFloat(), it.j.toFloat(), 1f, 1f)
                }
            }


            // Draw things on tiles
            for (i in (height - 1) downTo 0) {
                val x = i.toFloat()

                // Draw buildings
                for (j in 0 until width) {
                    val building = this[i, j]!!.building
                    if (building != null) {
                        val y = j.toFloat()

                        // Team outline
                        shape.color = building.team.color
                        shape.rect(x + 0.2f, y + 0.2f, 0.6f, 1f)

                        // Type infill
                        shape.color = building.color
                        shape.rect(x + 0.25f, y + 0.25f, 0.5f, 0.9f)

                    }
                }

                // Draw pawns
                for (j in 0 until width) {
                    val pawn = this[i, j]!!.pawn

                    if (pawn != null) {
                        val y = j.toFloat()

                        // Team outline
                        shape.color = pawn.team.color
                        shape.circle(x + 0.5f, y + 0.5f, 0.35f, 16)

                        // Type infill
                        shape.color = pawn.color
                        shape.circle(x + 0.5f, y + 0.5f, 0.3f, 16)
                    }
                }

            }

        }

        shape.end()

    }

}

data class SelectionSet(val coords: Set<WorldCoords>, val color: Color)
