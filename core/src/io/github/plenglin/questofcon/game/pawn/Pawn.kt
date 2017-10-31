package io.github.plenglin.questofcon.game.pawn

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.Team


abstract class PawnCreator(val name: String, val cost: Int) {

    abstract fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn

}

abstract class Pawn(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val actionPoints: Int, val color: Color) {

    var health: Int = maxHealth
        set(value) {
            field = value
            if (health <= 0) {
                println("$this died")
                pos.tile!!.pawn = null
            }
        }
    var apRemaining: Int = actionPoints

    fun getMovableSquares(): Set<WorldCoords> {
        return this.pos.floodfill(apRemaining, { it.tile!!.terrain.passable && it.tile.pawn == null })
    }

    abstract fun getAttackableSquares(): Set<WorldCoords>

    /**
     * Try to attack a square.
     * @param coords the square to attack
     * @return whether it was successful or not.
     */
    abstract fun onAttack(coords: WorldCoords): Boolean

    fun moveTo(coords: WorldCoords) {
        apRemaining -= Math.abs(coords.i - pos.i) + Math.abs(coords.j - pos.j)
        pos.tile!!.pawn = null  // clear old tile
        coords.tile!!.pawn = this  // set new tile to this
        pos = coords  // set this pawn's reference
    }

    open fun getProperties(): Map<String, Any> {
        return mapOf("type" to name, "team" to team.name, "health" to "$health/$maxHealth", "actions" to "$apRemaining/$actionPoints")
    }

    fun attack(coords: WorldCoords) {
        onAttack(coords)
    }

}


class SimplePawnCreator(name: String, cost: Int, val maxHealth: Int, val attack: Int, val color: Color, val actionPoints: Int = 2, val range: Int = 1) :
        PawnCreator(name, cost) {

    override fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn {
        val pawn = SimplePawn(team, worldCoords)
        worldCoords.tile!!.pawn = pawn
        return pawn
    }

    /**
     * A simple pawn that can be melee or ranged.
     */
    inner class SimplePawn(team: Team, pos: WorldCoords) : Pawn(name, team, pos, maxHealth, actionPoints, color) {

        override fun getAttackableSquares(): Set<WorldCoords> {
            return pos.floodfill(range).minus(this.pos)
        }

        override fun onAttack(coords: WorldCoords): Boolean {
            //val inRange = Math.abs(coords.i - this.pos.i) + Math.abs(coords.j - this.pos.j) <= range
            val tile = coords.tile
            if (tile != null && tile.getTeam() != this.team) {
                return tile.doDamage(attack)
            } else {
                return false
            }
        }

        override fun getProperties(): Map<String, Any> {
            val props = super.getProperties().toMutableMap()
            props["attack"] = attack
            if (range > 1) {
                props["range"] = range
            }
            return props
        }

    }

}