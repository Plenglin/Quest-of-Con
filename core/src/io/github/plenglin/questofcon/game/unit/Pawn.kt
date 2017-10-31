package io.github.plenglin.questofcon.game.unit

import io.github.plenglin.questofcon.game.grid.WorldCoords


interface PawnCreator {

    fun createPawnAt(worldCoords: WorldCoords): Pawn

}

abstract class Pawn(val name: String, var pos: WorldCoords, val maxHealth: Int, val actionPoints: Int) {

    var health: Int = maxHealth
    var apRemaining: Int = actionPoints

    abstract fun getAttackableSquares(): Set<WorldCoords>

    /**
     * Try to attack a square.
     * @param coords the square to attack
     * @return whether it was successful or not.
     */
    abstract fun onAttack(coords: WorldCoords): Boolean

    open fun getProperties(): Map<String, Any> {
        return mapOf("hp" to maxHealth, "ap" to actionPoints)
    }

}


class SimplePawnCreator(val name: String, val maxHealth: Int, val attack: Int, val actionPoints: Int = 2, val range: Int = 1) : PawnCreator {

    override fun createPawnAt(worldCoords: WorldCoords): Pawn {
        val pawn = SimplePawn(worldCoords)
        worldCoords.tile!!.pawn = pawn
        return pawn
    }

    /**
     * A simple pawn that can be melee or ranged.
     */
    inner class SimplePawn(pos: WorldCoords) : Pawn(name, pos, maxHealth, actionPoints) {

        override fun getAttackableSquares(): Set<WorldCoords> {
            return pos.floodfill(range)
        }

        override fun onAttack(coords: WorldCoords): Boolean {
            val inRange = Math.abs(coords.i - this.pos.i) + Math.abs(coords.j - this.pos.j) <= range
            val tile = coords.tile
            if (inRange && tile != null) {
                return tile.doDamage(attack)
            } else {
                return false
            }
        }

        override fun getProperties(): Map<String, Any> {
            val props = super.getProperties().toMutableMap()
            props["atk"] = attack
            if (range > 1) {
                props["range"] = range
            }
            return props
        }

    }

}