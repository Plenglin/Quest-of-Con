package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Registerable
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.net.DataBuilding
import io.github.plenglin.questofcon.ui.elements.ConfirmationDialog
import io.github.plenglin.questofcon.ui.elements.RadialMenuItem
import io.github.plenglin.questofcon.ui.UI
import java.io.Serializable


abstract class BuildingCreator(override val name: String, val displayName: String, val cost: Int) : Registerable {

    override var id: Long = -1

    abstract fun createBuildingAt(team: Team, worldCoords: WorldCoords, gameState: GameState): Building

}

var nextBuildingId = 0L

abstract class Building(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val gameState: GameState, val type: Long) {

    var id = nextBuildingId++

    abstract val texture: Texture?

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

    open fun getRadialActions() = listOf(RadialMenuItem("Demolish $name", {
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

    fun serialized(): Serializable? {
        return DataBuilding(id, team.id, type, health, enabled, pos.serialized())
    }

}
