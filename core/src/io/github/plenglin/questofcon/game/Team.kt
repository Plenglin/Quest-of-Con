package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.buildings.BuildingHQ
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords


class Team(val name: String, val color: Color) {

    var money: Int = Constants.STARTING_MONEY
    var hasBuiltHQ = false
    lateinit var world: World

    fun getBuildable(): List<BuildingCreator> {
        return if (hasBuiltHQ) GameData.spawnableBuildings else listOf(BuildingHQ)
    }

    fun getOwnedTiles(): List<WorldCoords> = world.filter { it.tile!!.getTeam() == this }.toList()

    fun getMoneyPerTurn(): Int = getOwnedTiles().sumBy { it.tile!!.building?.getMoneyPerTurn() ?: 0 }

    fun startTurn() {
        money += getMoneyPerTurn()
        getOwnedTiles().forEach {
            val building = it.tile!!.building
            if (building != null) {
                building.enabled = true
                building.onTurnBegin()
            }
            val pawn = it.tile.pawn
            if (pawn != null) {
                pawn.apRemaining = pawn.actionPoints
            }
        }
    }

    fun endTurn() {
        getOwnedTiles().forEach {
            it.tile!!.building?.onTurnEnd()
        }
    }

    override fun toString(): String {
        return "Team($name)"
    }

}