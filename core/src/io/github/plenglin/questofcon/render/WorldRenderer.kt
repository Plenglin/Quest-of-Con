package io.github.plenglin.questofcon.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords


class WorldRenderer(val world: World) {

    val batch: SpriteBatch = SpriteBatch()
    val shape: ShapeRenderer = ShapeRenderer()

    var elevation: Texture = generateElevation()

    fun generateElevation(): Texture {
        val pixmap = Pixmap(world.width, world.height, Pixmap.Format.RGBA8888)
        world.grid.forEachIndexed { i, col ->
            col.forEachIndexed { j, tile ->
                pixmap.setColor(1f, 1f, 1f, tile.elevation.toFloat() / Constants.ELEVATION_LEVELS)
                pixmap.drawPixel(i, world.height - j - 1)
            }
        }
        val out = Texture(pixmap)
        pixmap.dispose()
        return out
    }

    fun render(drawGrid: Boolean = true, vararg paints: ShadeSet) {

        shape.setAutoShapeType(true)

        world.apply {
            // Draw the biome
            batch.begin()
            batch.color = Color.WHITE
            grid.forEachIndexed { i, col ->
                val x = i.toFloat()
                col.forEachIndexed { j, tile ->
                    val y = j.toFloat()
                    batch.draw(tile.biome.texture.bg(), x, y, 1f, 1f)
                }
            }

            batch.setColor(1f, 1f, 1f, 0.75f)
            batch.draw(elevation, 0f, 0f, world.width.toFloat(), world.height.toFloat())

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
            shape.end()

            // Draw in the selection sets
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shape.begin()
            paints.forEach { it.draw(shape) }
            shape.end()
            Gdx.gl.glDisable(GL20.GL_BLEND);

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
                        batch.draw(building.texture, x + 0.1f, y + 0.1f, 0.8f, 0.8f)
                        val c = building.team.color
                        batch.setColor(c.r, c.g, c.b, 0.5f)
                        batch.draw(building.texture, x + 0.1f, y + 0.1f, 0.8f, 0.8f)
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

            // Draw pawns
            for (i in (height - 1) downTo 0) {
                val x = i.toFloat()

                for (j in 0 until width) {
                    val pawn = this[i, j]!!.pawn

                    if (pawn != null) {

                        val tex = pawn.texture() ?: Assets[Assets.missing]
                        val y = j.toFloat()

                        // Team outline
                        batch.color = Color.WHITE
                        batch.draw(tex, x, y, 1f, 1f)
                        val color = pawn.team.color.cpy()
                        color.a = 0.5f
                        batch.color = color
                        batch.draw(pawn.texture(), x, y, 1f, 1f)
                    }
                }

            }
            batch.end()

        }

    }

}

data class ShadeSet(
        val coords: Set<WorldCoords>,
        val shading: Color = Color.CLEAR,
        val mode: Int = SHADE,
        val lines: Color = Color.CLEAR) {

    fun draw(shape: ShapeRenderer) {

        // Draw shading
        shape.set(ShapeRenderer.ShapeType.Filled)
        if (mode and SHADE > 0) {
            coords.forEach {
                shape.color = shading
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