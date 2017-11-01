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

class PawnKnight(team: Team, pos: WorldCoords) : Pawn("KangarooBot", team, pos, 5, 2, Color.PINK) {

    override fun getAttackableSquares(): Set<WorldCoords> {
        return listOf(
            Pair( distA,  distB),
            Pair( distB,  distA),
            Pair(-distB,  distA),
            Pair(-distA,  distB),
            Pair(-distA, -distB),
            Pair(-distB, -distA),
            Pair( distB, -distA),
            Pair( distA, -distB)
        ).map { WorldCoords(pos.world, pos.i + it.first, pos.j + it.second) }.toSet()
    }

    override fun onAttack(coords: WorldCoords): Boolean {
        if (coords.tile!!.pawn != null) {
            return false
        }
        coords.floodfill(dmgRadius).forEach {
            it.tile!!.doDamage(damage)
        }
        moveTo(coords)
        return true
    }

    companion object : PawnCreator("KangarooBot", 50) {

        val distA = 3
        val distB = 2
        val damage = 5
        val dmgRadius = 1

        override fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn {
            val pawn = PawnKnight(team, worldCoords)
            worldCoords.tile!!.pawn = pawn
            return pawn
        }

    }
}
