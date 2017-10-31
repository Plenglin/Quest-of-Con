package io.github.plenglin.questofcon.game.unit

import io.github.plenglin.questofcon.game.grid.WorldCoords


interface PawnCreator {

    fun createPawnAt(worldCoords: WorldCoords): Pawn

}

abstract class Pawn(var pos: WorldCoords, val maxHealth: Int, val actionPoints: Int) {

    var health: Int = maxHealth
    var apRemaining: Int = actionPoints

    abstract fun getAttackableSquares(): Set<WorldCoords>

    abstract fun onAttack(coords: WorldCoords)

    fun getProperties(): Map<String, Any> {
        return mapOf("HP" to maxHealth, "AP" to actionPoints)
    }

}

class SimplePawn(pos: WorldCoords, maxHealth: Int, actionPoints: Int, val attack: Int, val range: Int = 1) : Pawn(pos, maxHealth, actionPoints) {

    override fun getAttackableSquares(): Set<WorldCoords> {
        return pos.floodfill(range)
    }

    override fun onAttack(coords: WorldCoords) {
        //if (coords.tile)
    }

}