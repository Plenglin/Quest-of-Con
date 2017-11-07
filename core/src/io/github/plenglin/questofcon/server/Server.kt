package io.github.plenglin.questofcon.server

import io.github.plenglin.questofcon.Constants
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 *
 */
class Server {

    val serverSocket = ServerSocket(Constants.SERVER_PORT)

    val pendingSockets = mutableListOf<Socket>()
    val rooms = mutableListOf<Room>()

    fun acceptSockets() {
        println("accepting sockets")
        while (true) {
            try {
                val sock = serverSocket.accept()
                println("A dude at ${sock.inetAddress} connected")
                pendingSockets.add(sock)

                if (pendingSockets.size >= 2) {
                    val room = Room(pendingSockets.toList())
                    room.start()
                    rooms.add(room)
                }
            } catch (e: IOException) {
                println("oh no err the orr!")
                e.printStackTrace()
            }
        }
    }

}

fun main(args: Array<String>) {

    Server().acceptSockets()

}