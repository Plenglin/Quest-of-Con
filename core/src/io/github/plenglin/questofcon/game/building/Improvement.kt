package io.github.plenglin.questofcon.game.building

import com.badlogic.gdx.graphics.Texture
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.BuildableType
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.Tile
import io.github.plenglin.questofcon.game.grid.WorldCoords
import java.io.Serializable

/**
 * @param aquatic Can it be built over water?
 * @param terrestrial Can it be built over land?
 * @param buildable Can you even build it?
 */
enum class Improvement(
        val aquatic: Boolean = false,
        val terrestrial: Boolean = true,
        val buildable: Boolean = true,
        val texture: (WorldCoords) -> Texture = { Assets[Assets.missing] }) : BuildableType<Tile>, Serializable {

    FOREST(buildable = false), JUNGLE(buildable = false),

    ROAD(false, true, texture = { Assets[Assets.road] }),
    CANAL(false, true, texture = { Assets[Assets.canal] }),
    BRIDGE(true, false, texture = { Assets[Assets.bridge] }),
    MINEFIELD(true, true);

    override fun buildAt(coords: WorldCoords, team: Team): Tile {
        return coords.tile!!
    }
}