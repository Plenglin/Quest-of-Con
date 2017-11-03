package io.github.plenglin.questofcon.game.grid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Textures


data class Terrain(val name: String, val texture: Textures, val passable: Boolean, val buildable: Boolean, val movementCost: Int = 1) {

    override fun toString(): String {
        return "Terrain($name)"
    }

}

object Terrains {

    val lowlands    = Terrain("lowlands", Textures.SAND, true, true)
    val grass       = Terrain("grassland", Textures.GRASS, true, true)
    val bigHills    = Terrain("big hills", Textures.BIGHILL, true, true, movementCost = 3)
    val hills       = Terrain("hills", Textures.SMALLHILL, true, true, movementCost = 2)
    val water       = Terrain("water", Textures.WATER, false, false, movementCost = 3)
    val mountains   = Terrain("mountains", Textures.MOUNTAIN, false, false)

    /*
    val forest      = Terrain("forest", Color.BROWN, true, false, movementCost = 2)
    val desert      = Terrain("desert", Color.GOLD, true, true, movementCost = 2)
    val gravel      = Terrain("gravel", Color.LIGHT_GRAY, true, true, movementCost = 2)
    val jungle      = Terrain("jungle", Color.BROWN, true, false, movementCost = 3)
    val swamp       = Terrain("swamp", Color(0f, 0.4f, 0.25f, 1f), true, true, movementCost = 3)
    */

}