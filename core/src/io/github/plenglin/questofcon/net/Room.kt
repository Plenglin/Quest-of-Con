package io.github.plenglin.questofcon.net

import com.beust.klaxon.Parser
import io.github.plenglin.questofcon.game.grid.World
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket
import java.util.logging.Logger


class Room(val sockets: List<Socket>, val roomId: Long) : Thread("Room-$roomId") {

    val world = World(32, 32)

    val logger = Logger.getLogger(javaClass.name)

    val clients = sockets.map { SocketManager(it, this) }

    override fun run() {
        logger.info("starting")
        logger.info("generating world")

        clients.forEach { it.start() }
    }

}

class SocketManager(val socket: Socket, val parent: Room) : Thread("SocketManager-${parent.roomId}-${socket.inetAddress}") {

    val logger = Logger.getLogger(this.name)

    var parser = Parser()

    lateinit var input: ObjectInputStream
    lateinit var output: ObjectOutputStream

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
                is ClientRequest -> send(onRequest(transmission.id, data))
                is ClientAction -> onAction(data)
            }

        }
    }

    private fun send(data: Serializable) {
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