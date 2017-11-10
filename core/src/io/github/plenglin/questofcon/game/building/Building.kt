package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.ui.ConfirmationDialog
import io.github.plenglin.questofcon.ui.Selectable
import io.github.plenglin.questofcon.ui.UI

abstract class BuildingCreator(val name: String, val cost: Int) {

    abstract fun createBuildingAt(team: Team, worldCoords: WorldCoords, gameState: GameState): Building

}

var nextBuildingId = 0L

abstract class Building(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val gameState: GameState) {

    val id = nextBuildingId++

    abstract val texture: Texture

    var enabled = true

    var health = maxHealth
        set(value) {
            field = value
            if (health <= 0) {
                pos.tile!!.building = null
            }
            gameState.buildingChange.fire(this)
        }

    open fun getMoneyPerTurn() = 0

    open fun onTurnBegin() = Unit

    open fun onTurnEnd() = Unit

    open fun getRadialActions() = listOf(Selectable("Demolish $name", {
        ConfirmationDialog("Demolish $name", UI.skin, {
            UI.targetPlayerInterface.demolishBuilding(this.id)
        }).show(UI.stage)
    }))

    open fun getProperties(): Map<String, Any> {
        val map = mutableMapOf("type" to name, "hp" to "$health/$maxHealth", "team" to team.name)
        val money = getMoneyPerTurn()
        if (money > 0) {
            map.put("Income", "$$money")
        }
        return map
    }

    open fun canCreate(type: PawnCreator): Boolean = false

}
