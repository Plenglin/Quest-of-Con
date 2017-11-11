package io.github.plenglin.questofcon.interop

import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.game.pawn.PawnCreator

class PassAndPlayManager(val state: GameState) {
    val interfaces = state.teams.map { PassAndPlayInterface(it.id, this) }

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

    override fun makePawn(at: WorldCoords, type: PawnCreator, onResult: (Pawn?) -> Unit) {
        val building = at.tile!!.building
        if (thisTeam.money >= type.cost && building?.canCreate(type) == true && building.team == thisTeam) {
            thisTeam.money -= type.cost
            val newPawn = type.createPawnAt(thisTeam, at, gameState)
            newPawn.apRemaining = 0
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

    override fun makeBuilding(at: WorldCoords, type: BuildingCreator, onResult: (Building?) -> Unit) {
        if (thisTeam.money < type.cost) {
            onResult(null)
            return
        }
        thisTeam.money -= type.cost
        val building = type.createBuildingAt(thisTeam, at, gameState)
        building.enabled = false
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

}