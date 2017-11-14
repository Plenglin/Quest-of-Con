package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.game.grid.WorldCoords

interface BuildableType<Output> {

    fun buildAt(coords: WorldCoords, team: Team): Output

}