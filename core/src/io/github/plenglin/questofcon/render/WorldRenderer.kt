package io.github.plenglin.questofcon.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords


class WorldRenderer(val world: World) {

    val batch: SpriteBatch = SpriteBatch()
    val shape: ShapeRenderer = ShapeRenderer()

    fun render(drawGrid: Boolean = true, vararg paints: ShadeSet) {

        shape.setAutoShapeType(true)

        world.apply {
            // Draw the terrain
            shape.begin(ShapeRenderer.ShapeType.Filled)
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
                    shape.color = s.shading
                    shape.rect(it.i.toFloat(), it.j.toFloat(), 1f, 1f)
                }
            }

            shape.end()

            // Draw buildings
            batch.enableBlending()
            batch.begin()
            for (i in (height - 1) downTo 0) {
                val x = i.toFloat()

                for (j in 0 until width) {
                    val building = this[i, j]!!.building
                    if (building != null) {
                        val y = j.toFloat()

                        batch.color = Color.WHITE
                        batch.draw(building.texture, x, y, 1f, 1f)
                        val c = building.team.color
                        batch.setColor(c.r, c.g, c.b, 0.5f)
                        batch.draw(building.texture, x, y, 1f, 1f)
                        /*
                        building.sprite.apply {
                            texture = building.texture
                            color = building.team.color
                            setBounds(x, y, 1f, 1f)
                            draw(batch)
                        }*/
                    }
                }
            }
            batch.end()

            // Draw pawns
            shape.begin(ShapeRenderer.ShapeType.Filled)
            for (i in (height - 1) downTo 0) {
                val x = i.toFloat()

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

data class ShadeSet(val coords: Set<WorldCoords>, val shading: Color)
