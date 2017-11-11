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
                    val pawn = GameData.pawnByType(data.type).createPawnAt(team, WorldCoords(parent.gameState.world, data.at), parent.gameState)
                    val ser = pawn.serialized()
                    //parent.broadcastEvent(ServerEventTypes.PAWN_CHANGE, ser)
                    return ServerResponse(msgId, ser)
                }
                ClientActions.MAKE_BUILDING -> {
                    data as DataBuildingCreation
                    val building = GameData.buildingByType(data.type).createBuildingAt(team, WorldCoords(parent.gameState.world, data.at), parent.gameState)
                    val ser = building.serialized()
                    //parent.broadcastEvent(ServerEventTypes.BUILDING_CHANGE, ser)
                    return ServerResponse(msgId, ser)
                }
                ClientActions.DEMOLISH_BUILDING -> {
                }
                ClientActions.MOVE_PAWN -> TODO()
                ClientActions.ATTACK_PAWN -> TODO()
                ClientActions.TALK -> TODO()
                ClientActions.END_TURN -> {
                    parent.gameState.nextTurn()
                }
            }
            return ServerResponse(msgId)
        } else {
            return ServerResponse(msgId, error = ServerResponseError.FORBIDDEN)
        }
    }

}