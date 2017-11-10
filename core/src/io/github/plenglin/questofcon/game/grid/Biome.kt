package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.TerrainTextures
import java.io.Serializable


private var nextBiomeId = 0L

data class Biome(val name: String, val texture: TerrainTextures, val passable: Boolean, val buildable: Boolean, val movementCost: Int = 1) {

    val id = nextBiomeId++

    override fun toString(): String {
        return "Biome($name)"
    }

}

object Biomes {

    val beach       = Biome("beach", TerrainTextures.SAND, true, false)
    val desert       = Biome("desert", TerrainTextures.SAND, true, false)
    val grass       = Biome("grassland", TerrainTextures.GRASS, true, true)
    val highlands   = Biome("highlands", TerrainTextures.BIGHILL, true, false, movementCost = 2)
    val water       = Biome("water", TerrainTextures.WATER, false, false)
    val mountains   = Biome("mountains", TerrainTextures.MOUNTAIN, false, false)

    val types = listOf(beach, desert, grass, highlands, water, mountains)

    fun getById(id: Long): Biome? {
        for (i in types) {
            if (i.id == id) {
                return i
            }
        }
        return null
    }

}
