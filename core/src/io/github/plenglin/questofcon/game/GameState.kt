package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.game.grid.World

/**
 *
 */
class GameState(val teams: List<Team>) {

    val world = World(32, 32)
    private var teamIndex = 0
    private var events = mutableListOf<Event>()

    init {
        teams.forEach {
            it.world = world
        }
        getCurrentTeam().startTurn()
    }

    fun getCurrentTeam(): Team {
        return teams[teamIndex]
    }

    fun nextTurn() {
        getCurrentTeam().endTurn()
        teamIndex = (teamIndex + 1) % teams.size
        getCurrentTeam().startTurn()
    }

}