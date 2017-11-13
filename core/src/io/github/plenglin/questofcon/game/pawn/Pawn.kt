package io.github.plenglin.questofcon.game.pawn

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.net.DataPawn
import io.github.plenglin.questofcon.ui.PawnActionManager
import io.github.plenglin.questofcon.ui.UI
import io.github.plenglin.questofcon.ui.elements.ConfirmationDialog
import io.github.plenglin.questofcon.ui.elements.RadialMenuItem


private var nextPawnId = 0L

class Pawn(val type: PawnType, _pos: WorldCoords) {

    var gameState: GameState? = null

    var id = nextPawnId++

    lateinit var team: Team

    open val maxAttacks = 1
    var attacksRemaining = 0
    var level = 0

    var pos: WorldCoords = _pos
        set(value) {
            field.tile!!.pawn = null
            field = value
            value.tile!!.pawn = this
        }

    var health: Int = type.maxHp(level)
        set(value) {
            field = value
            if (health <= 0) {
                pos.tile!!.pawn = null
            }
            gameState?.pawnChange?.fire(this)
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
            gameState?.pawnChange?.fire(this)
            return true
        }
        return false
    }

    open fun getProperties(): Map<String, Any> {
        return mapOf("type" to type.displayName, "team" to team.name, "health" to "$health/$maxHealth", "actions" to "$ap/$maxAp", "attacks" to "$attacksRemaining/$maxAttacks")
    }

    fun attemptAttack(coords: WorldCoords): Boolean {
        ap -= 1
        val result = onAttack(coords)
        if (result) {
            attacksRemaining -= 1
            gameState?.pawnChange?.fire(this)
        }
        return result
    }

    open fun getRadialActions(): List<RadialMenuItem> {

        val actions = mutableListOf<RadialMenuItem>(RadialMenuItem("Disband ${type.displayName}", {
            ConfirmationDialog("Disband ${type.displayName}", UI.skin, {
                UI.targetPlayerInterface.disbandPawn(this.id)
            }).show(UI.stage)
        }))

        if (ap > 0) {

            actions.add(RadialMenuItem("Move ${type.displayName}", {
                PawnActionManager.beginMoving(this)
            }))

            if (attacksRemaining > 0) {
                actions.add(RadialMenuItem("Attack with ${type.displayName}", {
                    PawnActionManager.beginAttacking(this)
                }))
            }

        }
        return actions
    }

    fun serialized(): DataPawn {
        return DataPawn(id, team.id, type.id, health, ap, attacksRemaining, pos.serialized())
    }

    override fun toString(): String {
        return "Pawn($id, ${javaClass.simpleName})"
    }

}