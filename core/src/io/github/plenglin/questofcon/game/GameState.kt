package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.game.grid.World

/**
 *
 */
class GameState(val teams: List<Team>) {

    val world = World(16, 16)
    private var teamIndex = 0

    fun getCurrentTeam(): Team {
        return teams[teamIndex]
    }

    fun nextTurn() {
        teamIndex = (teamIndex + 1) % teams.size
    }

}