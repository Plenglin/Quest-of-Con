package io.github.plenglin.questofcon.game.building

import io.github.plenglin.questofcon.game.BuildableType
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.util.Registerable

class BuildingType(_name: String) : Registerable, BuildableType<Building> {
    override fun buildAt(coords: WorldCoords): Building {
        val building = Building()
        return building
    }

    override var id: Long = -1
    override val name: String = _name

    var displayName = ""

    var maxHp = 1

    var cost = 0
    var upkeep = 0

    var power = 0

    var buildable: (Team) -> List<Pawn> = { emptyList() }

}