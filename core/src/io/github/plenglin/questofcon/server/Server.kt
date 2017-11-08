package io.github.plenglin.questofcon.server

import io.github.plenglin.questofcon.Constants
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketAddress
import java.util.logging.Level
import java.util.logging.Logger

/**
 *
 */
object Server {

    val port = Constants.SERVER_PORT

    var nextRoom = 0L

    val serverSocket = ServerSocket(port)

    val pendingSockets = mutableListOf<Socket>()
    val rooms = mutableListOf<Room>()

    fun acceptSockets() {
        println("accepting sockets on port $port")
        while (true) {
            val sock = serverSocket.accept()
            pendingSockets.add(sock)
            println("A dude at ${sock.inetAddress} connected, ${pendingSockets.size} dudes now")

            if (pendingSockets.size >= 2) {
                val room = Room(pendingSockets.toList(), nextRoom++)
                room.start()
                rooms.add(room)
                println("created room $room")
            }
        }
    }

}

fun main(args: Array<String>) {

    Thread(Runnable {Server.acceptSockets()}).start()
    Logger.getGlobal().level = Level.ALL

    Thread.sleep(2000)

    println("creating clients")
    val clients = mutableListOf<Client>()
    for (clientId in 0..1) {
        val socket = Socket("localhost", Constants.SERVER_PORT)
        val client = Client(socket)
        client.start()
        println("client $clientId started")
        clients.add(client)
    }
    Thread.sleep(1000)
    println("requesting from clients")
    clients.forEach {
        println("requesting")
        it.request(ClientRequestType.BUILDING, 1L, { println(it) })
    }

}