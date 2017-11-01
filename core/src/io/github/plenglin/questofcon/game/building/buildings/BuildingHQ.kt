package io.github.plenglin.questofcon.game.building.buildings

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.WorldCoords


class BuildingHQ(team: Team, pos: WorldCoords) : Building("HQ", team, pos, 100, Color.WHITE) {

    override fun getMoneyPerTurn(): Int = 15

    companion object : BuildingCreator("HQ", 0) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building {
            val building = BuildingHQ(team, worldCoords)
            worldCoords.tile!!.building = building
            team.hasBuiltHQ = true
            return building
        }

    }

}
