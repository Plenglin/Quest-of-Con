package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.game.grid.World

/**
 *
 */
class GameState(val teams: List<Team>) {

    val world = World(16, 16)
    val currentTeam: Team = teams[0]

}