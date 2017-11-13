package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.Improvement
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.net.DataTile

/**
 *
 */
class Tile {

    var biome: Biome = GameData.grass
    var elevation: Int = 0
    var pawn: Pawn? = null
    var building: Building? = null
    var improvement: Improvement? = null

    fun getTeam(): Team? {
        return pawn?.team ?: building?.team
    }

    fun passableBy(team: Team): Boolean {
        val tileTeam = getTeam()
        return biome.passable && (tileTeam == null || tileTeam == team)
    }

    fun canBuildOn(team: Team): Boolean {
        return biome.buildable && building == null && pawn?.let { it.team == team } != false
    }

    fun doDamage(hp: Int): Boolean {
        val bldg = building
        var output = false

        if (bldg != null) {
            bldg.health -= hp
            output = true
        }

        if (pawn != null) {
            pawn!!.health -= hp
            output = true
        }
        return output
    }

    fun serialized(): DataTile {
        return DataTile(biome.id, elevation)
    }

}