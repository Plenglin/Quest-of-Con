package io.github.plenglin.questofcon.server

import com.beust.klaxon.Parser
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.server.data.ClientActions
import io.github.plenglin.questofcon.server.data.DataClientAction
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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

    override fun run() {
        logger.info("starting ${this.name}")
        val input = ObjectInputStream(socket.getInputStream())
        val output = ObjectOutputStream(socket.getOutputStream())

        input.use { output.use {
            while (true) {
                val data = input.readObject() as DataClientAction
                logger.fine("rcv $data")

                when (data.action) {
                    ClientActions.READY -> {

                    }
                    ClientActions.TALK -> {
                        val msg = data.data as String
                        logger.info("TALK -> $msg")
                    }
                    ClientActions.MOVE -> TODO()
                    ClientActions.ATTACK -> TODO()
                }
            }
        } }
    }

}