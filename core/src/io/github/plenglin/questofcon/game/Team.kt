package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.util.ListenerManager
import io.github.plenglin.questofcon.game.building.BuildingType
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.net.DataTeam


private var nextTeamId = 0L

class Team(val name: String, val color: Color, _id: Long = -1) {

    val moneyChangeEvent = ListenerManager<Int>()
    val id = if (_id >= 0) _id else nextTeamId++
    var money: Int = Constants.STARTING_MONEY
        set(value) {
            field = value
            moneyChangeEvent.fire(field)
        }
    var hasBuiltHQ = false
    lateinit var world: World

    fun getBuildable(): List<BuildingType> {
        return if (hasBuiltHQ) GameData.buildings.toList().filter { it != GameData.hq } else listOf(GameData.hq)
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
                pawn.ap = pawn.maxAp
                pawn.attacksRemaining = pawn.maxAttacks
            }
        }
    }

    fun endTurn() {
        getOwnedTiles().forEach {
            it.tile!!.building?.onTurnEnd()
        }
    }

    override fun toString(): String {
        return "Team($id, $name)"
    }

    fun serialized(): DataTeam {
        return DataTeam(name, id, color.toIntBits())
    }

}