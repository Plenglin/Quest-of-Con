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
    val interfaces = mutableListOf<PassAndPlayInterface>()
}

class PassAndPlayInterface(override val thisTeam: Long, val parent: PassAndPlayManager) : PlayerInterface() {

    val gameState: GameState = parent.state
    override val world: World = gameState.world
    override val teams: MutableMap<Long, Team> = mutableMapOf(*gameState.teams.map { it.id to it }.toTypedArray())

    val team = teams[thisTeam]!!

    override fun makePawn(at: WorldCoords, type: PawnCreator, onResult: (Pawn?) -> Unit) {
        team.money -= type.cost
        val newPawn = type.createPawnAt(team, at)
        newPawn.apRemaining = 0
        onResult(newPawn)
    }

    override fun movePawn(id: Long, to: WorldCoords, onResult: (Boolean) -> Unit) {
        val pawn = getPawnData(id)!!
        onResult(pawn.moveTo(to, pawn.getMovableSquares()))
    }

    override fun attackPawn(id: Long, target: WorldCoords, onResult: (Boolean) -> Unit) {
        val pawn = getPawnData(id)!!
        onResult(pawn.getAttackableSquares().contains(target) && pawn.attemptAttack(target))
    }

    override fun makeBuilding(at: WorldCoords, type: BuildingCreator, onResult: (Building?) -> Unit) {
        team.money -= type.cost
        val building = type.createBuildingAt(team, at)
        building.enabled = false
        onResult(building)
    }

    override fun demolishBuilding(id: Long, onResult: (Boolean) -> Unit) {

    }

    override fun sendEndTurn(onResult: (Team) -> Unit) {
        gameState.nextTurn()
        onResult(gameState.getCurrentTeam())
    }

    override fun getAllPawns(): Sequence<Pawn> {
        return world.map { it.tile!!.pawn }.filterNotNull()
    }

    override fun getAllBuildings(): Sequence<Building> {
        return world.map { it.tile!!.building }.filterNotNull()
    }


}