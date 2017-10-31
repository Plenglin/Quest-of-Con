package io.github.plenglin.questofcon.game.grid

import com.badlogic.gdx.graphics.Color


enum class Movement {
    NORMAL, SLOWED, IMPASSABLE
}
/**
 *
 */
open class Terrain(val name: String, val color: Color, val passable: Boolean, val buildable: Boolean)

object Terrains {

    val grass = Terrain("grassland", Color.GREEN, true, true)
    val marsh = Terrain("hills", Color.BROWN, true, false)
    val mountains = Terrain("mountains", Color.BLACK, false, false)

}