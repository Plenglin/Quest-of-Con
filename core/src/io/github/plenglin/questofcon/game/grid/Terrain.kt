package io.github.plenglin.questofcon.game.grid

import com.badlogic.gdx.graphics.Color


open class Terrain(val name: String, val color: Color, val passable: Boolean, val buildable: Boolean, val movementCost: Int = 1)

object Terrains {

    val lowlands    = Terrain("lowlands", Color.TAN, true, true)
    val grass       = Terrain("grassland", Color(0f, 0.7f, 0f, 1f), true, true)
    val hills       = Terrain("hills", Color(0.3f, 0.5f, 0.1f, 1f), true, true, movementCost = 3)
    val water       = Terrain("water", Color(0.1f, 0.4f, 0.8f, 1f), false, false, movementCost = 3)
    val mountains   = Terrain("mountains", Color.BLACK, false, false)

    val forest      = Terrain("forest", Color.BROWN, true, false, movementCost = 2)
    val desert      = Terrain("desert", Color.GOLD, true, true, movementCost = 2)
    val gravel      = Terrain("gravel", Color.LIGHT_GRAY, true, true, movementCost = 2)
    val jungle      = Terrain("jungle", Color.BROWN, true, false, movementCost = 3)
    val swamp       = Terrain("swamp", Color(0f, 0.4f, 0.25f, 1f), true, true, movementCost = 3)

}