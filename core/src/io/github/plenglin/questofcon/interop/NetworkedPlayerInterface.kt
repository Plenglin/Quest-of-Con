package io.github.plenglin.questofcon.interop

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.ListenerManager
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.net.*


class NetworkedPlayerInterface(val client: Client) : PlayerInterface() {
    override val world: World
    override val teams: MutableMap<Long, Team>
    override val thisTeamId: Long

    val onTalk = ListenerManager<DataChat>()

    init {
        val resp = client.initialResponse
        val grid = resp.world.grid
        world = World(grid.size, grid[0].size)
        teams = mutableMapOf(*resp.teams.map { it.id to Team(it.name, Color(it.color), it.id) }.toTypedArray())
        thisTeamId = resp.yourId

        client.onServerEvent.addListener {
            val data = it.data
            when (it.action) {

                ServerEventTypes.BUILDING_CHANGE -> {
                    val bldgData = data as DataBuilding
                    var building = getBuildingData(bldgData.id)
                    if (building == null) {
                        val i = bldgData.pos.i
                        val j = bldgData.pos.j
                        building = GameData.buildingByType(bldgData.type).createBuildingAt(teams[bldgData.team]!!, WorldCoords(world, i, j), client.dummy)
                    }
                    building.team = teams[bldgData.team]!!
                    building.health = bldgData.health
                    buildingUpdate.fire(building)
                }

                ServerEventTypes.PAWN_CHANGE -> {
                    val pawnData = data as DataPawn
                    val i = pawnData.pos.i
                    val j = pawnData.pos.j
                    var pawn = getPawnData(data.id)
                    if (pawn == null) {
                        pawn = GameData.pawnByType(pawnData.type).createPawnAt(teams[pawnData.team]!!, WorldCoords(world, i, j), client.dummy)
                    }
                    pawn.team = teams[pawnData.team]!!
                    pawn.pos = WorldCoords(world, i, j)
                    pawn.health = pawnData.health
                    pawnUpdate.fire(pawn)
                }

                ServerEventTypes.TERRAIN_CHANGE -> TODO()
                ServerEventTypes.TALK -> onTalk.fire(it.data as DataChat)

            }
        }
    }

    override fun makePawn(at: WorldCoords, type: PawnCreator, onResult: (Pawn?) -> Unit) {
        client.action(ClientActions.MAKE_PAWN, DataPawnCreation(type.id, at.serialized()))
    }

    override fun movePawn(id: Long, to: WorldCoords, onResult: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun attackPawn(id: Long, target: WorldCoords, onResult: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun makeBuilding(at: WorldCoords, type: BuildingCreator, onResult: (Building?) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun demolishBuilding(id: Long, onResult: (Boolean) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendEndTurn(onResult: (Team) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllPawns(): Sequence<Pawn> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllBuildings(): Sequence<Building> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}