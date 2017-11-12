package io.github.plenglin.questofcon.game.pawn

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.Registerable
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.net.DataPawn
import io.github.plenglin.questofcon.ui.*
import io.github.plenglin.questofcon.ui.elements.ConfirmationDialog
import io.github.plenglin.questofcon.ui.elements.Selectable


abstract class PawnCreator(override val name: String, val displayName: String, val cost: Int) : Registerable {

    override var id: Long = -1
    abstract fun createPawnAt(team: Team, worldCoords: WorldCoords, state: GameState): Pawn

}

private var nextPawnId = 0L

abstract class Pawn(val name: String, var team: Team, _pos: WorldCoords, val maxHealth: Int, val maxAp: Int, val texture: () -> Texture?, val state: GameState) {

    var type = -1L
    var id = nextPawnId++

    open val maxAttacks = 1
    var attacksRemaining = 0

    var pos: WorldCoords = _pos
        set(value) {
            field.tile!!.pawn = null
            field = value
            value.tile!!.pawn = this
        }

    var health: Int = maxHealth
        set(value) {
            field = value
            if (health <= 0) {
                pos.tile!!.pawn = null
            }
            state.pawnChange.fire(this)
        }
    var ap: Int = 0

    fun getMovableSquares(): Map<WorldCoords, Int> {
        // Dijkstra
        val dist = mutableMapOf<WorldCoords, Int>(pos to 0)  // coord, cost
        val unvisited = pos.surrounding().filter { it.tile!!.passableBy(team) }.toMutableList()
        unvisited.forEach {
            dist[it] = it.tile!!.biome.movementCost + maxOf(it.tile.elevation - pos.tile!!.elevation, 1)
        }

        while (unvisited.isNotEmpty()) {
            val coord = unvisited.removeAt(0)  // Pop this new coordinate
            val tile = coord.tile!!
            val terrain = tile.biome
            val cost = terrain.movementCost
            //println("$terrain, ${cost}, ${tile.passableBy(team)}")
            val fullDist = dist[coord]!!

            if (tile.passableBy(team) && fullDist + cost <= ap) {  // Can we even get past this tile?
                coord.surrounding().forEach { neighbor ->  // For each neighbor...
                    val totalCost = fullDist + cost + maxOf(neighbor.tile!!.elevation - tile.elevation, 1)
                    val neighborDist = dist[neighbor]
                    val passable = neighbor.tile.passableBy(team)
                    //println("neigh: ${neighbor.tile.biome}, ${tile.building}, ${tile.passableBy(team)}")
                    if (passable) {
                        if (neighborDist == null) {  // If we haven't added the neighbor, add it now
                            unvisited.add(neighbor)
                            dist[neighbor] = fullDist + cost
                        } else if (neighborDist > totalCost) {  // Is going through coord to neighbor faster than before?
                            dist[neighbor] = fullDist + cost  // Put it in
                        }
                    }
                }
            }
        }

        //val keyset = dist.keys.subtract()

        return dist.filter { true }
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

    fun attemptMoveTo(coords: WorldCoords, movementData: Map<WorldCoords, Int>): Boolean {
        val cost = movementData[coords]
        if (cost != null) {
            return attemptMoveTo(coords, cost)
        } else {
            return false
        }
    }

    fun attemptMoveTo(coords: WorldCoords, apCost: Int): Boolean {
        if (ap - apCost >= 0) {
            ap -= apCost
            pos = coords
            state.pawnChange.fire(this)
            return true
        }
        return false
    }

    open fun getProperties(): Map<String, Any> {
        return mapOf("type" to name, "team" to team.name, "health" to "$health/$maxHealth", "actions" to "$ap/$maxAp", "attacks" to "$attacksRemaining/$maxAttacks")
    }

    fun attemptAttack(coords: WorldCoords): Boolean {
        ap -= 1
        val result = onAttack(coords)
        if (result) {
            attacksRemaining -= 1
            state.pawnChange.fire(this)
        }
        return result
    }

    open fun getRadialActions(): List<Selectable> {

        val actions = mutableListOf<Selectable>(Selectable("Disband $name", {
            ConfirmationDialog("Disband $name", UI.skin, {
                UI.targetPlayerInterface.disbandPawn(this.id)
            }).show(UI.stage)
        }))

        if (ap > 0) {

            actions.add(Selectable("Move $name", {
                PawnActionManager.beginMoving(this)
            }))

            if (attacksRemaining > 0) {
                actions.add(Selectable("Attack with $name", {
                    PawnActionManager.beginAttacking(this)
                }))
            }

        }
        return actions
    }

    fun serialized(): DataPawn {
        return DataPawn(id, team.id, type, health, ap, attacksRemaining, pos.serialized())
    }

    override fun toString(): String {
        return "Pawn($id, ${javaClass.simpleName})"
    }

    companion object {
        fun elevationDamageMultiplier(from: Int, to: Int): Double {
            val elevationChange = maxOf(to - from, 0)
            return minOf(Math.pow(1.125, -elevationChange.toDouble()), 1.0)
        }
    }

}

class SimplePawnCreator(name: String, displayName: String, cost: Int) : PawnCreator(name, displayName, cost) {

    var maxHealth: Int = 0
    var attack: Int = 0
    var texture: () -> Texture = { Assets.manager[Assets.missing] }
    var actionPoints: Int = 3
    var range: Int = 1
    var maxAttacks: Int = 1

    override fun createPawnAt(team: Team, worldCoords: WorldCoords, state: GameState): Pawn {
        val pawn = SimplePawn(team, worldCoords, state)
        worldCoords.tile!!.pawn = pawn
        pawn.type = id
        state.pawnChange.fire(pawn)
        return pawn
    }

    /**
     * A simple pawn that can be melee or ranged.
     */
    inner class SimplePawn(team: Team, pos: WorldCoords, state: GameState) : Pawn(displayName, team, pos, maxHealth, actionPoints, texture, state) {

        override fun damageTo(coords: WorldCoords): Int {
            val mult = Pawn.elevationDamageMultiplier(pos.tile!!.elevation, coords.tile!!.elevation)
            println(mult)
            return (attack * mult).toInt()
        }

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
                return tile.doDamage(damageTo(coords))
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

        override fun toString(): String {
            return "SimplePawn($id, $name)"
        }

    }

}
