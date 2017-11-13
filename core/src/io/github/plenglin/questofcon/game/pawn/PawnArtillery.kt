package io.github.plenglin.questofcon.game.pawn

import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords

class PawnArtillery(team: Team, pos: WorldCoords, state: GameState) : Pawn("Artillery", team, pos, 30, 1, { Assets[Assets.artillery] }, state) {

    override fun damageTo(coords: WorldCoords): Int = damage

    override fun getTargetingRadius(coords: WorldCoords): Set<WorldCoords> {
        return coords.floodfill(radius = dmgRadius)
    }

    override fun getAttackableSquares(): Set<WorldCoords> {
        return pos.floodfill(maxRange).subtract(pos.floodfill(minRange))
    }

    override fun onAttack(coords: WorldCoords): Boolean {
        coords.floodfill(dmgRadius).forEach {
            it.tile!!.doDamage(damage)
        }
        return true
    }

    companion object : PawnCreator("artillery", "Artillery", 500) {

        val damage = 100
        val dmgRadius = 1
        val maxRange = 5
        val minRange = 3

        override fun createPawnAt(team: Team, worldCoords: WorldCoords, state: GameState): Pawn {
            val pawn = PawnArtillery(team, worldCoords, state)
            pawn.type = id
            worldCoords.tile!!.pawn = pawn
            state.pawnChange.fire(pawn)
            return pawn
        }

    }
}