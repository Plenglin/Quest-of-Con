package io.github.plenglin.questofcon.game.building

import io.github.plenglin.questofcon.game.BuildableType
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.Tile
import io.github.plenglin.questofcon.game.grid.WorldCoords

enum class Improvement : BuildableType<Tile> {

    FOREST, JUNGLE,

    ROAD, CANAL, BRIDGES, RAILS, LANDMINES;

    override fun buildAt(coords: WorldCoords, team: Team): Tile {
        return coords.tile!!
    }
}