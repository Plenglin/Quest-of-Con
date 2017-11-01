package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.QuestOfCon
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords


class Team(val name: String, val color: Color) {

    var money: Int = 10
    lateinit var world: World

    fun getOwnedTiles(): List<WorldCoords> = world.filter { it.tile!!.getTeam() == this }.toList()

    fun startTurn() {
        money += QuestOfCon.BASE_ECO
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