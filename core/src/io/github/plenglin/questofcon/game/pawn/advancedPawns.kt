package io.github.plenglin.questofcon.game.pawn

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords


class PawnArtillery(team: Team, pos: WorldCoords) : Pawn("Bertha", team, pos, 3, 1, Color.BLACK) {

    override fun getAttackableSquares(): Set<WorldCoords> {
        return pos.floodfill(maxRange).subtract(pos.floodfill(minRange))
    }

    override fun onAttack(coords: WorldCoords): Boolean {
        coords.floodfill(dmgRadius).forEach {
            it.tile!!.doDamage(damage)
        }
        return true
    }

    companion object : PawnCreator("Bertha", 50) {

        val damage = 15
        val dmgRadius = 2
        val maxRange = 5
        val minRange = 3

        override fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn {
            val pawn = PawnArtillery(team, worldCoords)
            worldCoords.tile!!.pawn = pawn
            return pawn
        }

    }
}