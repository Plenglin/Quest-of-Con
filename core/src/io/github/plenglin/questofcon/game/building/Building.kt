package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords

interface BuildingCreator {

    fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building?

}

abstract class Building(val name: String, var team: Team, var pos: WorldCoords, val maxHealth: Int, val color: Color) {

    var health = 0

    open fun onTurnBegin() = Unit

    open fun onTurnEnd() = Unit

    open fun getProperties(): Map<String, Any> {
        return mapOf("hp" to health)
    }

}

class BuildingFactoryCreator : BuildingCreator {

    override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building? {
        val building = BuildingFactory(team, worldCoords)
        worldCoords.tile!!.building = building
        return building
    }

    class BuildingFactory(team: Team, pos: WorldCoords) : Building("factory", team, pos, 10, Color.GRAY) {

    }
}