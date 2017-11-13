package io.github.plenglin.questofcon.game.pawn

import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.util.Registerable

abstract class PawnCreator(override val name: String, val displayName: String, val cost: Int) : Registerable {

    override var id: Long = -1
    abstract fun createPawnAt(team: Team, worldCoords: WorldCoords, state: GameState): Pawn

}