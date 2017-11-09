package io.github.plenglin.questofcon.net

import com.badlogic.gdx.graphics.Color
import com.beust.klaxon.Parser
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.*
import io.github.plenglin.questofcon.screen.GameScreen
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket
import java.util.concurrent.CyclicBarrier
import java.util.logging.Logger


class Room(val sockets: List<Socket>, val roomId: Long) : Thread("Room-$roomId") {

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
            it.team = DataTeam(data.name, team.id, team.color.toIntBits())
            team
        })

        logger.info("generating world")

        generateWorld()

        logger.info("sending back data")
        sendInitialServerResponse()

    }

    fun sendInitialServerResponse() {
        clientsById.forEach { _, sock ->
            sock.send(DataInitialResponse(
                    sock.id,
                    clientsById.values.map { it.team },
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

    var parser = Parser()

    lateinit var input: ObjectInputStream
    lateinit var output: ObjectOutputStream

    lateinit var initialTransmission: DataInitialClientData
    lateinit var team: DataTeam

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
                is ClientAction -> onAction(data)
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
        return when (request.type) {
            ClientRequestType.BUILDING -> ServerResponse(request.type, DataBuilding(0, 0, 0, DataPosition(0, 0)), msgId)
            ClientRequestType.PAWN -> ServerResponse(request.type, DataPawn(0, 0, 0, DataPosition(0, 0)), msgId)
            else -> ServerResponse(request.type, request.key, msgId, ServerResponseError.DATA_ERROR)
        }
    }

    fun onAction(clientAction: ClientAction) {

    }

}