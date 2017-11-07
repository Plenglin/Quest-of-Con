package io.github.plenglin.questofcon.server

import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.server.data.ClientActions
import io.github.plenglin.questofcon.server.data.DataAction
import java.io.IOException
import java.io.ObjectOutputStream
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
            val sock = serverSocket.accept()
            println("A dude at ${sock.inetAddress} connected")
            pendingSockets.add(sock)

            if (pendingSockets.size >= 2) {
                val room = Room(pendingSockets.toList())
                room.start()
                rooms.add(room)
            }
        }
    }

}

fun main(args: Array<String>) {

    Thread(Runnable {Server().acceptSockets()}).start()

    for (client in 0..1) {
        Thread(Runnable {
            Thread.sleep(1000)
            println("client $client starting")
            val sock = Socket("localhost", Constants.SERVER_PORT)
            val output = ObjectOutputStream(sock.getOutputStream())
            Thread.sleep(1000)
            println("client $client writing shit")
            output.writeObject(DataAction(ClientActions.TALK, "MEMES"))
        }).start()
    }

}