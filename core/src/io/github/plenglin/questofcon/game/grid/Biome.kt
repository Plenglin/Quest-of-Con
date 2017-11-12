package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.Registerable
import io.github.plenglin.questofcon.TerrainTextures
import java.io.Serializable


data class Biome(override val name: String, val displayName: String, val texture: TerrainTextures, val passable: Boolean, val buildable: Boolean, val movementCost: Int = 1) : Registerable {
    override var id: Long = -1L

    override fun toString(): String {
        return "Biome($name)"
    }

}

