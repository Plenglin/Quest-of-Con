package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.TerrainTextures


data class Terrain(val name: String, val texture: TerrainTextures, val passable: Boolean, val buildable: Boolean, val movementCost: Int = 1) {

    override fun toString(): String {
        return "Terrain($name)"
    }

}

object Terrains {

    val sandy       = Terrain("sandy", TerrainTextures.SAND, true, false)
    val grass       = Terrain("grassland", TerrainTextures.GRASS, true, true)
    val bigHills    = Terrain("big hills", TerrainTextures.BIGHILL, true, false, movementCost = 3)
    val hills       = Terrain("hills", TerrainTextures.SMALLHILL, true, true, movementCost = 2)
    val water       = Terrain("water", TerrainTextures.WATER, false, false, movementCost = 3)
    val mountains   = Terrain("mountains", TerrainTextures.MOUNTAIN, false, false)

}