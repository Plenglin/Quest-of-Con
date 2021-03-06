package io.github.plenglin.questofcon.game.building

import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.PawnType
import io.github.plenglin.questofcon.net.DataBuilding
import io.github.plenglin.questofcon.ui.UI
import io.github.plenglin.questofcon.ui.elements.ConfirmationDialog
import io.github.plenglin.questofcon.ui.elements.RadialMenuItem
import io.github.plenglin.questofcon.ui.elements.UnitSpawningDialog
import java.io.Serializable


var nextBuildingId = 0L

class Building(val type: BuildingType, var team: Team, var pos: WorldCoords) {

    var id = nextBuildingId++
    var gameState: GameState? = null

    var enabled = true

    var health = type.maxHp
        set(value) {
            field = value
            if (health <= 0) {
                pos.tile!!.building = null
            }
            gameState?.buildingChange?.fire(this)
        }

    fun applyToPosition(gameState: GameState? = null): Building {
        pos.tile!!.building = this
        this.gameState = gameState
        return this
    }

    fun getMoneyPerTurn() = 0

    fun onTurnBegin() = Unit

    fun onTurnEnd() = Unit

    fun getRadialActions(): List<RadialMenuItem> {
        val out = mutableListOf<RadialMenuItem>()
        if (this.type != GameData.hq) {
            out.add(RadialMenuItem("Demolish $displayName", {
                ConfirmationDialog("Demolish $displayName", UI.skin, {
                    UI.targetPlayerInterface.demolishBuilding(this.id)
                }).show(UI.stage)
            }))
        }
        val buildables = this.buildable
        if (buildables.isNotEmpty()) {
            out.add(RadialMenuItem("Make", {
                UnitSpawningDialog(buildables, UI.skin, pos, team).show(UI.stage)
            }))
        }
        return out
    }

    fun getProperties(): Map<String, Any> {
        val map = mutableMapOf("type" to type.displayName, "hp" to "$health/${type.maxHp}", "team" to team.name)
        val money = getMoneyPerTurn()
        if (money > 0) {
            map.put("Income", "$$money")
        }
        return map
    }

    fun serialized(): Serializable? {
        return DataBuilding(id, team.id, type.id, health, enabled, pos.serialized())
    }

    val buildable get(): List<PawnType> = type.buildable(team)
    val texture get() = type.texture()
    val displayName = type.displayName

}
