package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.pawn.Pawn

/**
 *
 */
class Tile(var terrain: Terrain) {

    var pawn: Pawn? = null
    var building: Building? = null

    fun getTeam(): Team? {
        return pawn?.team ?: building?.team
    }

    fun canBuildOn(team: Team): Boolean {
        return building == null && pawn?.let { it.team == team } ?: true
    }

    fun doDamage(hp: Int): Boolean {
        val bldg = building
        if (bldg != null) {
            bldg.health -= hp
            return true
        }

        if (pawn != null) {
            println(pawn!!.team.name)
            pawn!!.health -= hp
            return true
        }
        return false
    }

}