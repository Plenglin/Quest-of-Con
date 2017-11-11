package io.github.plenglin.questofcon.net

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.*
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket
import java.util.concurrent.CyclicBarrier
import java.util.logging.Logger


class Room(val sockets: List<Socket>, val roomId: Long) : Thread("Room-$roomId"), Iterable<SocketManager> {
    override fun iterator(): Iterator<SocketManager> {
        return clientsById.values.iterator()
    }

    lateinit var gameState: GameState
    val colors = mutableListOf<Color>(Color.RED, Color.GREEN, Color.BLUE)
    val barrier = CyclicBarrier(sockets.size + 1)
    val logger = Logger.getLogger(javaClass.name)

    val clientsById = mutableMapOf<Long, SocketManager>()

    override fun run() {
        logger.info("${name} starting")
        logger.info("$name connecting to clients")

        barrier.reset()

        val clients = sockets.map { SocketManager(it, this) }
        clients.forEach { it.start() }

        logger.info("$name waiting for clients to send initial message")
        barrier.await()

        logger.info("$name all clients sent info, now creating teams")

        gameState = GameState(clients.map {
            val data = it.initialTransmission
            val team = Team(data.name, colors.removeAt(0))
            logger.info("$name made team $team")
            clientsById.put(team.id, it)
            it.team = team
            team
        })

        gameState.turnChange.addListener {
            broadcastEvent(ServerEventTypes.CHANGE_TURN, it.id)
        }
        gameState.pawnChange.addListener {
            broadcastEvent(ServerEventTypes.PAWN_CHANGE, it.serialized())
        }
        gameState.buildingChange.addListener {
            broadcastEvent(ServerEventTypes.BUILDING_CHANGE, it.serialized())
        }
        gameState.worldChange.addListener {
            broadcastEvent(ServerEventTypes.TERRAIN_CHANGE, gameState.world.serialized())
        }

        logger.info("generating world")

        generateWorld()

        logger.info("sending back data")
        sendInitialServerResponse()

    }

    fun broadcastEvent(eventType: ServerEventTypes, data: Serializable? = null) {
        logger.info("bcast $eventType: $data")
        forEach { it.send(ServerEvent(eventType, data)) }
    }

    private fun sendInitialServerResponse() {
        clientsById.forEach { _, sockMan ->
            sockMan.send(DataInitialResponse(
                    sockMan.team.id,
                    clientsById.values.map { it.team.serialized() },
                    gameState.world.serialized()
            ))
        }
    }

    fun generateWorld() {
        val heightData = HeightMap(DiamondSquareHeightGenerator(3, initialOffsets = 2.0, iterativeRescale = 0.8).generate().grid).normalized
        val rainfallData = HeightMap(DiamondSquareHeightGenerator(3, initialOffsets = 2.0, iterativeRescale = 0.8).generate().grid).normalized

        println("Mapping height data to world...")
        MapToHeight(gameState.world, heightData).doHeightMap()

        println("Adding biomes...")
        BiomeGenerator(gameState.world, heightData, rainfallData).applyBiomes()

    }
}

class SocketManager(val socket: Socket, val parent: Room) : Thread("SocketManager-${parent.roomId}-${socket.inetAddress}") {

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