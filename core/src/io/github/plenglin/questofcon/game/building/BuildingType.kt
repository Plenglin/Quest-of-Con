package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.BuildableType
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.PawnType
import io.github.plenglin.util.Registerable

class BuildingType(_name: String) : Registerable, BuildableType<Building> {
    override fun buildAt(coords: WorldCoords, team: Team): Building {
        val building = Building(this, team, coords).applyToPosition()
        return building
    }

    override var id: Long = -1
    override val name: String = _name

    var displayName = ""

    var maxHp = 1

    var cost = 0
    var upkeep = 0

    var power = 0

    var buildable: (Team) -> List<PawnType> = { emptyList() }

    var texture: () -> Texture = Assets.missingno

}