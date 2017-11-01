package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.ConfirmationDialog
import io.github.plenglin.questofcon.ui.Selectable
import io.github.plenglin.questofcon.ui.UI
import io.github.plenglin.questofcon.ui.UnitSpawningDialog

abstract class BuildingCreator(val name: String, val cost: Int) {

    abstract fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building

}

abstract class Building(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val color: Color) {

    var enabled = true

    var health = maxHealth
        set(value) {
            field = value
            if (health <= 0) {
                println("$this died")
                pos.tile!!.building = null
            }
        }

    open fun getMoneyPerTurn() = 0

    open fun onTurnBegin() = Unit

    open fun onTurnEnd() = Unit

    open fun getActions(): List<Selectable> {
        return listOf(
                object : Selectable("Demolish") {
                    override fun onSelected(x: Float, y: Float) {
                        ConfirmationDialog("Demolish", UI.skin, {
                            health = 0
                        }).show(UI.stage)
                    }
                }
        )
    }

    open fun getProperties(): Map<String, Any> {
        return mapOf("hp" to "$health/$maxHealth", "team" to team.name)
    }

}
