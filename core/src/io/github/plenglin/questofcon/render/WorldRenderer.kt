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
            batch.begin()
            batch.color = Color.WHITE
            grid.forEachIndexed { i, col ->
                col.forEachIndexed { j, tile ->
                    batch.draw(tile.terrain.texture(), i.toFloat(), j.toFloat(), 1f, 1f)
                }
            }
            batch.end()

            // Draw the grid if necessary
            shape.begin(ShapeRenderer.ShapeType.Filled)
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

            // Draw in the selection sets
            paints.forEach { it.draw(shape) }

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

data class ShadeSet(
        val coords: Set<WorldCoords>,
        val shading: Color = Color.CLEAR,
        val mode: Int = SHADE,
        val lines: Color = Color.CLEAR) {

    fun draw(shape: ShapeRenderer) {

        // Draw shading
        shape.color = shading
        shape.set(ShapeRenderer.ShapeType.Filled)
        if (mode and SHADE > 0) {
            coords.forEach {
                shape.rect(it.i.toFloat(), it.j.toFloat(), 1f, 1f)
            }
        }

        // Draw inner lines
        shape.color = lines
        if (mode and INNER_LINES > 0) {
            coords.map {
                val x = it.i.toFloat()
                val y = it.j.toFloat()
                listOf(
                        Line(x, y, x + 1, y),
                        Line(x, y, x, y + 1),
                        Line(x + 1, y, x + 1, y + 1),
                        Line(x, y + 1, x + 1, y + 1)
                )}.flatten().toSet().forEach {
                it.draw(shape, width = 0.03f)
            }
        }

        // Draw outlines
        if (mode and OUTLINE > 0) {
            val lines = coords.map { c ->
                c.surrounding().filter {  // Get all the cells surrounding it that aren't inside the set
                    !coords.contains(it)
                }.map { surr ->  // Get the line dividing the two
                    val dx = surr.i - c.i
                    val dy = surr.j - c.j
                    val output = when (dx) {
                        +1 -> Line(c.i + 1, c.j, c.i + 1, c.j + 1)
                        -1 -> Line(c.i, c.j, c.i, c.j + 1)
                        else -> when (dy) {
                            +1 -> Line(c.i, c.j + 1, c.i + 1, c.j + 1)
                            else -> Line(c.i, c.j, c.i + 1, c.j)
                        }
                    }
                    output
                }
            }.flatten()
            lines.forEach {
                it.draw(shape, width = 0.05f)
            }
        }

    }

    companion object {
        val SHADE = 1
        val OUTLINE = 1 shl 2
        val INNER_LINES = 1 shl 3
    }

}

data class Line(val x1: Float, val y1: Float, val x2: Float, val y2: Float) {

    private fun toList() = listOf(x1, y1, x2, y2)
    private fun toListReverse() = listOf(x2, y2, x1, y1)

    constructor(x1: Int, y1: Int, x2: Int, y2: Int) : this(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())

    override operator fun equals(other: Any?): Boolean {
        when (other) {
            is Line -> {
                val otherList = other.toList()
                return (otherList == this.toList()) || (otherList == this.toListReverse())
            }
        }
        return false
    }

    fun draw(shape: ShapeRenderer, width: Float = 0f) {
        shape.rectLine(this.x1, this.y1, this.x2, this.y2, width)
    }

    override fun hashCode(): Int {
        var result = x1.hashCode()
        result = 31 * result + y1.hashCode()
        result = 31 * result + x2.hashCode()
        result = 31 * result + y2.hashCode()
        return result
    }

}