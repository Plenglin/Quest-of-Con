package io.github.plenglin.questofcon.game.grid

import com.badlogic.gdx.graphics.Color


enum class Movement {
    NORMAL, SLOWED, IMPASSABLE
}
/**
 *
 */
open class Terrain(val name: String, val color: Color, val movement: Movement)

object Terrains {

    val grass = Terrain("grassland", Color.GREEN, Movement.NORMAL)
    val hills = Terrain("hills", Color.BROWN, Movement.SLOWED)
    val mountains = Terrain("mountains", Color.BLACK, Movement.IMPASSABLE)

}