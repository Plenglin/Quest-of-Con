package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.GdxRuntimeException
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.Textures
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.elements.Selectable


class BuildingHQ(team: Team, pos: WorldCoords, gameState: GameState, type: Long) : Building("Headquarters", team, pos, Constants.HQ_HEALTH, gameState, type) {

    override val texture: Texture? = try { Textures.HEADQUARTERS() } catch (e: GdxRuntimeException) { null }

    override fun getMoneyPerTurn(): Int = Constants.BASE_ECO

    override fun getRadialActions(): List<Selectable> {
        return emptyList()  // No demolishing the HQ!
    }

    companion object : BuildingCreator("bldg-hq", "HQ", 0) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords, gameState: GameState): Building {
            val building = BuildingHQ(team, worldCoords, gameState, id)
            worldCoords.tile!!.building = building
            team.hasBuiltHQ = true
            gameState.buildingChange.fire(building)
            return building
        }

    }

}
