package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Textures
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords

class BuildingMine(team: Team, pos: WorldCoords, gameState: GameState, type: Long) : Building("Mine", team, pos, 50, gameState, type) {

    override val texture: Texture = Textures.MINE()

    override fun getMoneyPerTurn(): Int = 50

    companion object : BuildingCreator("Mine", 250) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords, gameState: GameState): Building {
            val building = BuildingMine(team, worldCoords, gameState, id)
            worldCoords.tile!!.building = building
            return building
        }

    }

}