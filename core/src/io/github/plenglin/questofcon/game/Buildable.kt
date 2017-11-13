package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.game.grid.WorldCoords

interface Buildable {

    fun buildAt(coords: WorldCoords)

}