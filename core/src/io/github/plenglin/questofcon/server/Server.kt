package io.github.plenglin.questofcon.server

import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.server.data.ClientActions
import io.github.plenglin.questofcon.server.data.DataClientAction
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.logging.Level
import java.util.logging.Logger

/**
 *
 */
object Server {

    var nextRoom = 0L

    val serverSocket = ServerSocket(Constants.SERVER_PORT)

    val pendingSockets = mutableListOf<Socket>()
    val rooms = mutableListOf<Room>()

    fun acceptSockets() {
        println("accepting sockets")
        while (true) {
            val sock = serverSocket.accept()
            println("A dude at ${sock.inetAddress} connected")
            pendingSockets.add(sock)

            if (pendingSockets.size >= 2) {
                val room = Room(pendingSockets.toList(), nextRoom++)
                room.start()
                rooms.add(room)
            }
        }
    }

}

fun main(args: Array<String>) {

    Thread(Runnable {Server.acceptSockets()}).start()
    Logger.getGlobal().level = Level.ALL

    for (client in 0..1) {
        Thread(Runnable {
            Thread.sleep(1000)
            println("client $client starting")
            val sock = Socket("localhost", Constants.SERVER_PORT)
            val output = ObjectOutputStream(sock.getOutputStream())
            Thread.sleep(1000)
            println("client $client writing shit")
            output.writeObject(DataClientAction(ClientActions.TALK, "MEMES"))
        }).start()
    }

}