package io.github.plenglin.questofcon.interop

import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.net.DataPawn


class PassAndPlayInterface(override val thisTeam: Long, val gameState: GameState) : PlayerInterface() {

    override val world: World = gameState.world
    override val teams: MutableMap<Long, Team> = mutableMapOf(*gameState.teams.map { it.id to it }.toTypedArray())

    override fun makePawn(at: WorldCoords, onResult: (DataPawn?) -> Unit) {

    }

    override fun movePawn(id: Long, to: WorldCoords, onResult: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun attackPawn(id: Long, target: WorldCoords, onResult: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun makeBuilding(at: WorldCoords, onResult: (Long?) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun demolishBuilding(id: Long, onResult: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendEndTurn(onResult: (Team) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMovableSquares(id: Long): Set<WorldCoords> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttackableSquares(id: Long): Set<WorldCoords> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTargetingRadius(id: Long, pos: WorldCoords): Set<WorldCoords> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPawnData(id: Long): Pawn {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllPawns(): List<Pawn> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllBuildings(): List<Building> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}