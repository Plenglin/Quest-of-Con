package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Textures
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.Selectable
import io.github.plenglin.questofcon.ui.UI
import io.github.plenglin.questofcon.ui.UnitSpawningDialog

class BuildingFactory(team: Team, pos: WorldCoords) : Building("factory", team, pos, 100) {

    override val texture: Texture = Textures.FACTORY()

    override fun getRadialActions(): List<Selectable> {
        return super.getRadialActions() + if (pos.tile!!.pawn == null) listOf(
            Selectable("Make", {
                UI.stage.addActor(UnitSpawningDialog(GameData.spawnableUnits, UI.skin, pos, team))
            })
        ) else emptyList()
    }

    companion object : BuildingCreator("factory", 200) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building {
            val building = BuildingFactory(team, worldCoords)
            worldCoords.tile!!.building = building
            return building
        }

    }
}
