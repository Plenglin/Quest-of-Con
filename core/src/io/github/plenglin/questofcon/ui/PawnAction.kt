package io.github.plenglin.questofcon.ui

import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn


data class PawnAction(val pawn: Pawn, val squares: Set<WorldCoords>) {

}