package io.github.plenglin.questofcon.game.building.buildings

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.Textures
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.Selectable
import ktx.assets.getValue
import ktx.assets.load


class BuildingHQ(team: Team, pos: WorldCoords) : Building("HQ", team, pos, 100) {

    override val texture: Texture = Textures.HEADQUARTERS()

    override fun getMoneyPerTurn(): Int = 15

    override fun getActions(): List<Selectable> {
        return emptyList()  // No demolishing the HQ!
    }

    companion object : BuildingCreator("HQ", 0) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building {
            val building = BuildingHQ(team, worldCoords)
            worldCoords.tile!!.building = building
            team.hasBuiltHQ = true
            return building
        }

    }

}
