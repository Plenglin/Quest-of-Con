package io.github.plenglin.questofcon.game

import io.github.plenglin.util.ListenerManager
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingType
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.game.pawn.PawnType
import io.github.plenglin.questofcon.net.DataChat


abstract class PlayerInterface {
    
    abstract val world: World
    abstract val teams: MutableMap<Long, Team>
    abstract val thisTeamId: Long
    abstract val thisTeam: Team

    val turnChange: ListenerManager<Team> = ListenerManager()
    val pawnUpdate: ListenerManager<Pawn> = ListenerManager()
    val buildingUpdate: ListenerManager<Building> = ListenerManager()
    val worldUpdate: ListenerManager<Unit> = ListenerManager()
    val chatUpdate: ListenerManager<DataChat> = ListenerManager()

    // Actions
    abstract fun makePawn(at: WorldCoords, type: PawnType, onResult: (Pawn?) -> Unit = {})
    abstract fun movePawn(id: Long, to: WorldCoords, onResult: (Boolean) -> Unit = {})
    abstract fun attackPawn(id: Long, target: WorldCoords, onResult: (Boolean) -> Unit = {})
    abstract fun disbandPawn(id: Long, onResult: (Boolean) -> Unit = {})

    abstract fun makeBuilding(at: WorldCoords, type: BuildingType, onResult: (Building?) -> Unit = {})
    abstract fun demolishBuilding(id: Long, onResult: (Boolean) -> Unit = {})
    abstract fun sendEndTurn(onResult: (Team) -> Unit = {})

    abstract fun sendChat(text: String, onResult: (Boolean) -> Unit = {})

    // Data
    fun getPawnData(id: Long): Pawn? {
        return getAllPawns().find { it.id == id }
    }

    fun getBuildingData(id: Long): Building? {
        return getAllBuildings().find { it.id == id }
    }

    abstract fun getAllPawns(): Sequence<Pawn>
    abstract fun getAllBuildings(): Sequence<Building>
    abstract fun getCurrentTeam(): Team

    fun isCurrentTurn(): Boolean {
        return getCurrentTeam() == thisTeam
    }

}