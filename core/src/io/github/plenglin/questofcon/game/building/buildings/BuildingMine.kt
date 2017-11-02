package io.github.plenglin.questofcon.game.building.buildings

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.Textures
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.WorldCoords
import ktx.assets.getValue
import ktx.assets.load
import ktx.assets.loadOnDemand

class BuildingMine(team: Team, pos: WorldCoords) : Building("Mine", team, pos, 5) {

    override val texture: Texture = Textures.MINE()

    override fun getMoneyPerTurn(): Int = 10

    override fun getProperties(): Map<String, Any> {
        return super.getProperties() + mapOf(
                "income" to getMoneyPerTurn()
        )
    }

    companion object : BuildingCreator("Mine", 30) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building {
            val building = BuildingMine(team, worldCoords)
            worldCoords.tile!!.building = building
            return building
        }

    }

}