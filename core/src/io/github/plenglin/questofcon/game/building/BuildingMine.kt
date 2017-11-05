package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Textures
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords

class BuildingMine(team: Team, pos: WorldCoords) : Building("Mine", team, pos, 50) {

    override val texture: Texture = Textures.MINE()

    override fun getMoneyPerTurn(): Int = 50

    companion object : BuildingCreator("Mine", 25) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building {
            val building = BuildingMine(team, worldCoords)
            worldCoords.tile!!.building = building
            return building
        }

    }

}