package io.github.plenglin.questofcon.game.building

import io.github.plenglin.questofcon.game.Buildable
import io.github.plenglin.questofcon.game.grid.WorldCoords

enum class Improvement : Buildable {

    FOREST, JUNGLE,

    ROAD, CANAL, BRIDGES, RAILS, LANDMINES;

    override fun buildAt(coords: WorldCoords) {

    }
}