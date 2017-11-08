package io.github.plenglin.questofcon.game.pawn

import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords


class PawnArtillery(team: Team, pos: WorldCoords) : Pawn("Artillery", team, pos, 30, 1, { Assets[Assets.artillery] }) {

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

    companion object : PawnCreator("Artillery", 500) {

        val damage = 100
        val dmgRadius = 1
        val maxRange = 5
        val minRange = 3

        override fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn {
            val pawn = PawnArtillery(team, worldCoords)
            pawn.type = id
            worldCoords.tile!!.pawn = pawn
            return pawn
        }

    }
}

class PawnKnight(team: Team, pos: WorldCoords) : Pawn("KangarooBot", team, pos, 50, 1, { Assets[Assets.kangaroobot] }) {

    override fun damageTo(coords: WorldCoords): Int = damage

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

    override fun getTargetingRadius(coords: WorldCoords) = coords.floodfill(dmgRadius)

    override fun onAttack(coords: WorldCoords): Boolean {
        val pawn = coords.tile!!.pawn
        val building = coords.tile.building
        if (pawn != null) {
            if (pawn.team != this.team && pawn.health - damage > 0) {
                return false
            }
        }
        if (building != null && building.team != this.team) {
            return false
        }
        coords.floodfill(dmgRadius).forEach {
            it.tile!!.doDamage(damage)
        }
        moveTo(coords, apCost = actionPoints)
        return true
    }

    companion object : PawnCreator("KangarooBot", 300) {

        val distA = 3
        val distB = 2
        val damage = 50
        val dmgRadius = 1

        override fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn {
            val pawn = PawnKnight(team, worldCoords)
            pawn.type = id
            worldCoords.tile!!.pawn = pawn
            return pawn
        }

    }
}
