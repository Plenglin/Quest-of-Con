package io.github.plenglin.questofcon.game.building

import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.util.Registerable

abstract class BuildingCreator(override val name: String, val displayName: String, val cost: Int) : Registerable {

    override var id: Long = -1

    abstract fun createBuildingAt(team: Team, worldCoords: WorldCoords, gameState: GameState): Building

}