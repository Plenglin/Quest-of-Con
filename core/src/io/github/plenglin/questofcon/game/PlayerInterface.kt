package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.ListenerManager
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.net.DataBuilding
import io.github.plenglin.questofcon.net.DataPawn
import io.github.plenglin.questofcon.net.DataTeam


abstract class PlayerInterface {
    
    abstract val world: World
    abstract val teams: MutableMap<Long, Team>
    abstract val thisTeam: Long

    val onTurnChange: ListenerManager<Team> = ListenerManager()
    val onPawnUpdate: ListenerManager<Pawn> = ListenerManager()
    val onBuildingUpdate: ListenerManager<DataBuilding> = ListenerManager()

    // Actions
    abstract fun makePawn(at: WorldCoords, onResult: (DataPawn?) -> Unit = {})
    abstract fun movePawn(id: Long, to: WorldCoords, onResult: (Boolean) -> Unit = {})
    abstract fun attackPawn(id: Long, target: WorldCoords, onResult: (Boolean) -> Unit = {})
    abstract fun makeBuilding(at: WorldCoords, onResult: (Long?) -> Unit)
    abstract fun demolishBuilding(id: Long, onResult: (Boolean) -> Unit)
    abstract fun sendEndTurn(onResult: (Team) -> Unit)

    // Data
    abstract fun getMovableSquares(id: Long): Set<WorldCoords>
    abstract fun getAttackableSquares(id: Long): Set<WorldCoords>
    abstract fun getTargetingRadius(id: Long, pos: WorldCoords): Set<WorldCoords>
    abstract fun getPawnData(id: Long): Pawn

    abstract fun getAllPawns(): List<Pawn>
    abstract fun getAllBuildings(): List<Building>

}