package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.TerrainTextures


data class Biome(val name: String, val texture: TerrainTextures, val passable: Boolean, val buildable: Boolean, val movementCost: Int = 1) {

    override fun toString(): String {
        return "Biome($name)"
    }

}

object Biomes {

    val sandy       = Biome("sandy", TerrainTextures.SAND, true, false)
    val grass       = Biome("grassland", TerrainTextures.GRASS, true, true)
    val bigHills    = Biome("big hills", TerrainTextures.BIGHILL, true, false, movementCost = 3)
    val hills       = Biome("hills", TerrainTextures.SMALLHILL, true, true, movementCost = 2)
    val water       = Biome("water", TerrainTextures.WATER, false, false, movementCost = 3)
    val mountains   = Biome("mountains", TerrainTextures.MOUNTAIN, false, false)

}