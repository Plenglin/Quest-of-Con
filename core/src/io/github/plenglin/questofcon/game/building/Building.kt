package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.Selectable
import io.github.plenglin.questofcon.ui.UI
import io.github.plenglin.questofcon.ui.UnitSpawningDialog

interface BuildingCreator {

    fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building?

}

abstract class Building(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val color: Color) {

    var health = 0

    open fun onTurnBegin() = Unit

    open fun onTurnEnd() = Unit

    open fun getActions(): List<Selectable> {
        return listOf()
    }

    open fun getProperties(): Map<String, Any> {
        return mapOf("hp" to "$health/$maxHealth", "team" to team.name)
    }

}

class BuildingFactoryCreator : BuildingCreator {

    override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building? {
        val building = BuildingFactory(team, worldCoords)
        worldCoords.tile!!.building = building
        return building
    }

    class BuildingFactory(team: Team, pos: WorldCoords) : Building("factory", team, pos, 10, Color.GRAY) {

        override fun getActions(): List<Selectable> {
            return if (pos.tile!!.pawn == null) listOf(
                    object : Selectable("Make") {
                        override fun onSelected(x: Float, y: Float) {
                            UI.stage.addActor(UnitSpawningDialog(GameData.spawnableUnits, UI.skin, pos, team))
                        }
                    }
            ) else emptyList()
        }

    }
}