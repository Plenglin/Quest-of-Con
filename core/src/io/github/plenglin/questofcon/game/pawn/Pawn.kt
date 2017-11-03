package io.github.plenglin.questofcon.game.pawn

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords


abstract class PawnCreator(val name: String, val cost: Int) {

    abstract fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn

}

abstract class Pawn(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val actionPoints: Int, val color: Color) {

    open val maxAttacks = 1
    var attacksRemaining = 0

    var health: Int = maxHealth
        set(value) {
            field = value
            if (health <= 0) {
                pos.tile!!.pawn = null
            }
        }
    var apRemaining: Int = actionPoints

    fun getMovableSquares(): Map<WorldCoords, Int> {
        // Dijkstra
        val dist = mutableMapOf<WorldCoords, Int>(pos to 0)  // coord, cost
        val unvisited = pos.surrounding().filter { it.tile!!.passableBy(team) }.toMutableList()
        unvisited.forEach {
            dist[it] = it.tile!!.terrain.movementCost
        }

        while (unvisited.isNotEmpty()) {
            val coord = unvisited.removeAt(0)  // Pop this new coordinate
            val tile = coord.tile!!
            val terrain = tile.terrain
            val cost = terrain.movementCost
            println("$terrain, ${tile.building}, ${tile.passableBy(team)}")
            val fullDist = dist[coord]!!

            if (tile.passableBy(team) && fullDist + cost <= apRemaining) {  // Can we even get past this tile?
                coord.surrounding().forEach { neighbor ->  // For each neighbor...
                    val alt = fullDist + cost
                    val neighborDist = dist[neighbor]
                    val passable = neighbor.tile!!.passableBy(team)
                    println("neigh: ${neighbor.tile.terrain}, ${tile.building}, ${tile.passableBy(team)}")
                    if (passable) {
                        if (neighborDist == null) {  // If we haven't added the neighbor, add it now
                            unvisited.add(neighbor)
                            dist[neighbor] = fullDist + cost
                        } else if (neighborDist > alt) {  // Is going through coord to neighbor faster than before?
                            dist[neighbor] = fullDist + cost  // Put it in
                        }
                    }
                }
            }
        }

        return dist
    }

    abstract fun getAttackableSquares(): Set<WorldCoords>

    open fun getTargetingRadius(coords: WorldCoords): Set<WorldCoords> = setOf(coords)

    abstract fun damageTo(coords: WorldCoords): Int

    /**
     * Try to attemptAttack a square.
     * @param coords the square to attemptAttack
     * @return whether it was successful or not.
     */
    abstract fun onAttack(coords: WorldCoords): Boolean

    fun moveTo(coords: WorldCoords, movementData: Map<WorldCoords, Int>) {
        moveTo(coords, movementData[coords]!!)
    }

    fun moveTo(coords: WorldCoords, apCost: Int) {
        apRemaining -= apCost
        pos.tile!!.pawn = null  // clear old tile
        coords.tile!!.pawn = this  // set new tile to this
        pos = coords  // set this pawn's reference
    }

    open fun getProperties(): Map<String, Any> {
        return mapOf("type" to name, "team" to team.name, "health" to "$health/$maxHealth", "actions" to "$apRemaining/$actionPoints", "attacks" to "$attacksRemaining/$maxAttacks")
    }

    fun attemptAttack(coords: WorldCoords): Boolean {
        apRemaining -= 1
        val result = onAttack(coords)
        if (!result) {
            attacksRemaining -= 1
        }
        return result
    }

}


class SimplePawnCreator(name: String, cost: Int, val maxHealth: Int, val attack: Int, val color: Color, val actionPoints: Int = 3, val range: Int = 1, val maxAttacks: Int = 1) :
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

        override fun damageTo(coords: WorldCoords): Int = attack

        override val maxAttacks = this@SimplePawnCreator.maxAttacks

        override fun getAttackableSquares(): Set<WorldCoords> {
            return pos.floodfill(range).minus(this.pos)
        }

        override fun getTargetingRadius(coords: WorldCoords): Set<WorldCoords> {
            return setOf(coords)
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