package io.github.plenglin.questofcon.ui

import io.github.plenglin.questofcon.game.pawn.Pawn


data class PawnMovement(val pawn: Pawn) {

    val squares = pawn.getMovableSquares()

}