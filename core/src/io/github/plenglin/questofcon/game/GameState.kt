package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.ListenerManager
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.pawn.Pawn

/**
 *
 */
open class GameState(val teams: List<Team>) {

    val world = World(32, 32)
    private var teamIndex = 0
    private var events = mutableListOf<Event>()

    val turnChange = ListenerManager<Team>()
    val pawnChange = ListenerManager<Pawn>()
    val buildingChange = ListenerManager<Building>()
    val worldChange = ListenerManager<World>()

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
        getAllPawns().forEach { pawnChange.fire(it) }
        getAllBuildings().forEach { buildingChange.fire(it) }
        turnChange.fire(getCurrentTeam())
    }

    fun queueEvent(event: Event) {
        events.add(event)
    }

    fun processEvents() {
        while (!events.isEmpty()) {
            val e = events.removeAt(0)
            when (e) {
                is PawnChangeEvent -> pawnChange.fire(getAllPawns().find { it.id == e.id }!!)
                is BuildingChangeEvent -> buildingChange.fire(getAllBuildings().find { it.id == e.id }!!)
                is WorldChangeEvent -> worldChange.fire(world)
            }
        }
    }

    fun getAllPawns(): Sequence<Pawn> {
        return world.map { it.tile!!.pawn }.filterNotNull()
    }

    fun getAllBuildings(): Sequence<Building> {
        return world.map { it.tile!!.building }.filterNotNull()
    }

}

class DummyGameState : GameState(listOf(Team("heu", Color.CLEAR))) {
    init {

    }
}