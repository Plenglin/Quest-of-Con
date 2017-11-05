package io.github.plenglin.questofcon.game.pawn

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.*


abstract class PawnCreator(val title: String, val cost: Int) {

    abstract fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn

}

abstract class Pawn(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val actionPoints: Int, val texture: () -> Texture) {

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

    fun moveTo(coords: WorldCoords, movementData: Map<WorldCoords, Int>): Boolean {
        val cost = movementData[coords]
        if (cost != null) {
            return moveTo(coords, cost)
        } else {
            return false
        }
    }

    fun moveTo(coords: WorldCoords, apCost: Int): Boolean {
        if (apRemaining - apCost >= 0) {
            apRemaining -= apCost
            pos.tile!!.pawn = null  // clear old tile
            coords.tile!!.pawn = this  // set new tile to this
            pos = coords  // set this pawn's reference
            return true
        }
        return false
    }

    open fun getProperties(): Map<String, Any> {
        return mapOf("type" to name, "team" to team.name, "health" to "$health/$maxHealth", "actions" to "$apRemaining/$actionPoints", "attacks" to "$attacksRemaining/$maxAttacks")
    }

    fun attemptAttack(coords: WorldCoords): Boolean {
        apRemaining -= 1
        val result = onAttack(coords)
        if (result) {
            attacksRemaining -= 1
        }
        return result
    }

    open fun getRadialActions(): List<Selectable> {

        val actions = mutableListOf<Selectable>(Selectable("Disband $name", {
            ConfirmationDialog("Disband $name", UI.skin, {
                health = 0
            }).show(UI.stage)
        }))

        if (apRemaining > 0) {

            actions.add(Selectable("Move $name", {
                PawnActionInputManager.setPawnState(
                        this,
                        PawnActionInputManager.State.MOVE
                )
            }))

            if (attacksRemaining > 0) {
                actions.add(Selectable("Attack with $name", {
                    PawnActionInputManager.setPawnState(
                            this,
                            PawnActionInputManager.State.ATTACK
                    )
                }))
            }

        }
        return actions
    }

    /**
     * The action bound to the Q key
     */
    var primaryAction: () -> Boolean = {
        if (apRemaining > 0 && attacksRemaining > 0) {
            PawnActionInputManager.setPawnState(
                    this,
                    PawnActionInputManager.State.ATTACK
            )
            true
        }
        false
    }

    /**
     * The action bound to the E key
     */
    var secondaryAction: () -> Boolean = {
        if (apRemaining > 0) {
            PawnActionInputManager.setPawnState(
                    this,
                    PawnActionInputManager.State.MOVE
            )
            true
        }
        false
    }

}


class SimplePawnCreator(name: String, cost: Int) : PawnCreator(name, cost) {

    var maxHealth: Int = 0
    var attack: Int = 0
    var texture: () -> Texture = { Assets.manager[Assets.missing] }
    var actionPoints: Int = 3
    var range: Int = 1
    var maxAttacks: Int = 1

    override fun createPawnAt(team: Team, worldCoords: WorldCoords): Pawn {
        val pawn = SimplePawn(team, worldCoords)
        worldCoords.tile!!.pawn = pawn
        return pawn
    }

    /**
     * A simple pawn that can be melee or ranged.
     */
    inner class SimplePawn(team: Team, pos: WorldCoords) : Pawn(title, team, pos, maxHealth, actionPoints, texture) {

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