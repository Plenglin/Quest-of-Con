package io.github.plenglin.questofcon.interop

import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingType
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.game.pawn.PawnType

class PassAndPlayManager(val state: GameState) {
    val interfaces = state.teams.map { PassAndPlayInterface(it.id, this) }
    fun currentInterface() = interfaces.find { it.thisTeam == state.getCurrentTeam() }!!

    init {
        state.pawnChange.addListener { p ->
            interfaces.forEach { it.pawnUpdate.fire(p) }
        }
        state.buildingChange.addListener { b ->
            interfaces.forEach { it.buildingUpdate.fire(b) }
        }
        state.worldChange.addListener { p ->
            interfaces.forEach { it.worldUpdate.fire(Unit) }
        }
    }
}

class PassAndPlayInterface(override val thisTeamId: Long, val parent: PassAndPlayManager) : PlayerInterface() {

    val gameState: GameState = parent.state
    override val world: World = gameState.world
    override val teams: MutableMap<Long, Team> = mutableMapOf(*gameState.teams.map { it.id to it }.toTypedArray())
    override val thisTeam: Team = teams[thisTeamId]!!

    override fun makePawn(at: WorldCoords, type: PawnType, onResult: (Pawn?) -> Unit) {
        val building = at.tile!!.building
        if (thisTeam.money >= type.cost && building?.team == thisTeam && building.buildable.contains(type)) {
            thisTeam.money -= type.cost
            val newPawn = Pawn(type, thisTeam, at).applyToPosition()
            newPawn.ap = 0
            onResult(newPawn)
        } else {
            onResult(null)
        }
    }

    override fun movePawn(id: Long, to: WorldCoords, onResult: (Boolean) -> Unit) {
        val pawn = getPawnData(id)!!
        if (pawn.team == this.thisTeam) {
            onResult(pawn.attemptMoveTo(to, pawn.getMovableSquares()))
        }
    }

    override fun attackPawn(id: Long, target: WorldCoords, onResult: (Boolean) -> Unit) {
        val pawn = getPawnData(id)!!
        if (pawn.team == this.thisTeam) {
            onResult(pawn.getAttackableSquares().contains(target) && pawn.attemptAttack(target))
        }
    }

    override fun makeBuilding(at: WorldCoords, type: BuildingType, onResult: (Building?) -> Unit) {
        if (thisTeam.money < type.cost) {
            onResult(null)
            return
        }
        thisTeam.money -= type.cost
        val building = Building(type, thisTeam, at).applyToPosition()
        //building.enabled = false
        gameState.buildingChange.fire(building)
        onResult(building)
    }

    override fun demolishBuilding(id: Long, onResult: (Boolean) -> Unit) {
        val building = getBuildingData(id)!!
        if (building.team == thisTeam) {
            building.health = 0
            onResult(true)
        }
        onResult(false)
    }

    override fun sendEndTurn(onResult: (Team) -> Unit) {
        gameState.nextTurn()
        onResult(gameState.getCurrentTeam())
    }

    override fun getAllPawns(): Sequence<Pawn> {
        return gameState.getAllPawns()
    }

    override fun getAllBuildings(): Sequence<Building> {
        return gameState.getAllBuildings()
    }

    override fun getCurrentTeam(): Team {
        return gameState.getCurrentTeam()
    }

    override fun disbandPawn(id: Long, onResult: (Boolean) -> Unit) {
        getPawnData(id)!!.health = 0
    }

    override fun sendChat(text: String, onResult: (Boolean) -> Unit) {
        onResult(false)
    }

}