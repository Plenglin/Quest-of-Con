package io.github.plenglin.questofcon.interop

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.ListenerManager
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.Biomes
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.net.*


class NetworkedPlayerInterface(val client: Client) : PlayerInterface() {

    override val world: World
    override val teams: MutableMap<Long, Team>
    override val thisTeamId: Long
    override val thisTeam: Team

    val onTalk = ListenerManager<DataChat>()

    init {
        val resp = client.initialResponse
        val grid = resp.world.grid
        world = World(grid.size, grid[0].size)

        world.forEach {
            val i = it.i
            val j = it.j
            val data = grid[i][j]
            it.tile!!.let {
                it.elevation = data.elevation
                it.biome = Biomes.getById(data.biome)!!
            }
        }

        teams = mutableMapOf(*resp.teams.map {
            println("${it.id} -> ${it.name}")
            it.id to Team(it.name, Color(it.color), it.id) }.toTypedArray())
        teams.values.forEach { it.world = world }
        thisTeamId = resp.yourId
        println(thisTeamId)
        thisTeam = teams[thisTeamId]!!

        client.onTurnChanged.addListener {
            currentTeam = it.id
        }

        client.onServerEvent.addListener {
            val data = it.data
            when (it.action) {

                ServerEventTypes.BUILDING_CHANGE -> {
                    val bldgData = data as DataBuilding
                    val i = bldgData.pos.i
                    val j = bldgData.pos.j
                    var building = getBuildingData(bldgData.id)
                    if (building == null) {
                        building = GameData.buildingByType(bldgData.type).createBuildingAt(teams[bldgData.team]!!, WorldCoords(world, i, j), client.dummy)
                        building.id = bldgData.id
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
                        pawn.id = pawnData.id
                    }
                    pawn.ap = pawnData.ap
                    pawn.team = teams[pawnData.team]!!
                    pawn.pos = WorldCoords(world, i, j)
                    pawn.health = pawnData.health
                    pawnUpdate.fire(pawn)
                }

                ServerEventTypes.TERRAIN_CHANGE -> TODO()

                ServerEventTypes.CHANGE_TURN -> TODO()
                ServerEventTypes.TALK -> onTalk.fire(it.data as DataChat)
            }
        }
    }

    private var currentTeam: Long = 0

    override fun getCurrentTeam(): Team {
        return teams[currentTeam]!!
    }

    override fun makePawn(at: WorldCoords, type: PawnCreator, onResult: (Pawn?) -> Unit) {
        client.action(ClientActions.MAKE_PAWN, DataPawnCreation(type.id, at.serialized()))
    }

    override fun movePawn(id: Long, to: WorldCoords, onResult: (Boolean) -> Unit) {
        client.action(ClientActions.MOVE_PAWN, DataPawnMovement(id, to.serialized()), respOk(onResult))
    }

    override fun attackPawn(id: Long, target: WorldCoords, onResult: (Boolean) -> Unit) {
        client.action(ClientActions.ATTACK_PAWN, target.serialized(), respOk(onResult))
    }

    override fun makeBuilding(at: WorldCoords, type: BuildingCreator, onResult: (Building?) -> Unit) {
        client.action(ClientActions.MAKE_BUILDING, DataBuildingCreation(type.id, at.serialized()))
    }

    override fun demolishBuilding(id: Long, onResult: (Boolean) -> Unit) {
        client.action(ClientActions.DEMOLISH_BUILDING, id, respOk(onResult))
    }

    override fun sendEndTurn(onResult: (Team) -> Unit) {
        client.action(ClientActions.END_TURN)
    }

    override fun getAllPawns(): Sequence<Pawn> {
        return world.map { it.tile!!.pawn }.filterNotNull()
    }

    override fun getAllBuildings(): Sequence<Building> {
        return world.map { it.tile!!.building }.filterNotNull()
    }

    fun respOk(onResult: (Boolean) -> Unit): (ServerResponse) -> Unit {
        return { onResult(it.error == ServerResponseError.OK) }
    }

}