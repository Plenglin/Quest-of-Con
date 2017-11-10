package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.Textures
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.Selectable


class BuildingHQ(team: Team, pos: WorldCoords, gameState: GameState) : Building("Headquarters", team, pos, Constants.HQ_HEALTH, gameState) {

    override val texture: Texture = Textures.HEADQUARTERS()

    override fun getMoneyPerTurn(): Int = Constants.BASE_ECO

    override fun getRadialActions(): List<Selectable> {
        return emptyList()  // No demolishing the HQ!
    }

    companion object : BuildingCreator("HQ", 0) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords, gameState: GameState): Building {
            val building = BuildingHQ(team, worldCoords, gameState)
            worldCoords.tile!!.building = building
            team.hasBuiltHQ = true
            return building
        }

    }

}
