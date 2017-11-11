package io.github.plenglin.questofcon.net

import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket
import java.util.logging.Logger


class SocketManager(val socket: Socket, val parent: GameRoom) : Thread("SocketManager-${parent.roomId}-${socket.inetAddress}") {

    val logger = Logger.getLogger(this.name)

    lateinit var input: ObjectInputStream
    lateinit var output: ObjectOutputStream

    lateinit var initialTransmission: DataInitialClientData
    lateinit var team: Team

    override fun run() {
        logger.info("starting ${this.name}")

        output = ObjectOutputStream(socket.getOutputStream())
        input = ObjectInputStream(socket.getInputStream())

        parent.initializationFinished.addListener {
            println("meme start meme")
            team.moneyChangeEvent.addListener {
                logger.info("$name money is $it")
                send(DataTeamBalance(it))
            }
        }
        logger.info("listening to ${this.name}")
        while (true) {
            val transmission = input.readObject() as Transmission
            val data = transmission.payload
            logger.info("rcv $transmission")
            when (data) {
                is DataInitialClientData -> {
                    initialTransmission = data
                    parent.barrier.await()
                }
                is ClientRequest -> send(onRequest(transmission.id, data))
                is ClientAction -> send(onAction(transmission.id, data))
            }

        }
    }

    fun send(data: Serializable) {
        output.writeObject(Transmission(getNextId(), data))
    }

    private var nextTransmissionId = 0L

    private fun getNextId(): Long {
        return nextTransmissionId++
    }

    fun onRequest(msgId: Long, request: ClientRequest): ServerResponse {
        /*//val data = request.da
        return when (request.type) {
            ClientRequestType.BUILDING -> {
                GameData.buildingByType()parent.gameState
                ServerResponse(request.type, DataBuilding(0, 0, 0, DataPosition(0, 0)), msgId)
            }
            ClientRequestType.PAWN -> ServerResponse(request.type, DataPawn(0, 0, 0, DataPosition(0, 0)), msgId)
            else -> ServerResponse(request.type, request.key, msgId, ServerResponseError.DATA_ERROR)
        }*/
        return ServerResponse(msgId, ServerResponseError.ID_DOES_NOT_EXIST)  // TODO: PLACEHOLDER
    }

    fun onAction(msgId: Long, action: ClientAction): ServerResponse {
        val data = action.data
        if (action.action == ClientActions.TALK || parent.gameState.getCurrentTeam() == team) {
            when (action.action) {
                ClientActions.MAKE_PAWN -> {
                    //if (parent.gameState)
                    data as DataPawnCreation
                    val creator = GameData.pawnByType(data.type)
                    team.money -= creator.cost
                    val pawn = creator.createPawnAt(team, WorldCoords(parent.gameState.world, data.at), parent.gameState)
                    val ser = pawn.serialized()
                    //parent.broadcastEvent(ServerEventTypes.PAWN_CHANGE, ser)
                    return ServerResponse(msgId, ser)
                }
                ClientActions.MAKE_BUILDING -> {
                    data as DataBuildingCreation
                    val creator = GameData.buildingByType(data.type)
                    team.money -= creator.cost
                    val building = creator.createBuildingAt(team, WorldCoords(parent.gameState.world, data.at), parent.gameState)
                    val ser = building.serialized()
                    //parent.broadcastEvent(ServerEventTypes.BUILDING_CHANGE, ser)
                    return ServerResponse(msgId, ser)
                }
                ClientActions.DEMOLISH_BUILDING -> {
                    val id = data as Long
                    parent.gameState.getAllBuildings().find { it.id == id }!!.health = 0
                    return ServerResponse(msgId, true)
                }
                ClientActions.MOVE_PAWN -> {
                    data as DataPawnMovement
                    val pawn = parent.gameState.getAllPawns().find { it.id == data.id }!!
                    val success = pawn.attemptMoveTo(WorldCoords(parent.gameState.world, data.to), pawn.getMovableSquares())
                    return ServerResponse(msgId, success)
                }
                ClientActions.ATTACK_PAWN -> {
                    data as DataPawnAttack
                    val pawn = parent.gameState.getAllPawns().find { it.id == data.id }!!
                    val success = pawn.attemptAttack(WorldCoords(parent.gameState.world, data.pos))
                    return ServerResponse(msgId, success)
                }
                ClientActions.END_TURN -> {
                    logger.info("ending le turn")
                    parent.changeTurn()
                    return ServerResponse(msgId, true)
                }
                ClientActions.DISBAND_PAWN -> {
                    val id = data as Long
                    parent.gameState.getAllPawns().find { it.id == id }!!.health = 0
                    return ServerResponse(msgId, true)
                }
                ClientActions.TALK -> TODO()
            }
        } else {
            return ServerResponse(msgId, error = ServerResponseError.FORBIDDEN)
        }
    }

}