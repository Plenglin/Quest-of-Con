package io.github.plenglin.questofcon.game.building.buildings

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.WorldCoords

class BuildingMine(team: Team, pos: WorldCoords) : Building("Mine", team, pos, 5, Color.GOLD) {

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