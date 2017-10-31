package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.unit.Pawn

/**
 *
 */
class Tile(var terrain: Terrain) {

    var pawn: Pawn? = null
    var building: Building? = null

    fun doDamage(hp: Int): Boolean {
        val bldg = building
        if (bldg != null) {
            bldg.health -= hp
            return true
        }

        val p = pawn
        if (p != null) {
            p.health -= hp
            return true
        }
        return false
    }

}