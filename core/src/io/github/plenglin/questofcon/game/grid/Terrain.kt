package io.github.plenglin.questofcon.game.grid

import com.badlogic.gdx.graphics.Color


open class Terrain(val name: String, val color: Color, val passable: Boolean, val buildable: Boolean, movementCost: Int = 1)

object Terrains {

    val grass       = Terrain("grassland", Color.GREEN, true, true)
    val lowlands    = Terrain("lowlands", Color.TAN, true, true)
    val forest      = Terrain("forest", Color.BROWN, true, false, movementCost = 2)
    val desert      = Terrain("desert", Color.GOLD, true, true, movementCost = 2)
    val gravel      = Terrain("gravel", Color.LIGHT_GRAY, true, true, movementCost = 2)
    val jungle      = Terrain("jungle", Color.BROWN, true, false, movementCost = 3)
    val hills       = Terrain("hills", Color.DARK_GRAY, true, true, movementCost = 3)
    val swamp       = Terrain("swamp", Color(0f, 0.4f, 0.25f, 1f), true, true, movementCost = 3)
    val water       = Terrain("water", Color(0.1f, 0.4f, 0.8f, 1f), false, false, movementCost = 3)
    val mountains   = Terrain("mountains", Color.BLACK, false, false)
    val snowyPeak   = Terrain("snowy mountains", Color.BLACK, false, false)

}