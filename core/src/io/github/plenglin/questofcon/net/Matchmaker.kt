package io.github.plenglin.questofcon.net

import io.github.plenglin.questofcon.Constants
import java.net.ServerSocket
import java.net.Socket
import java.util.logging.Level
import java.util.logging.Logger

/**
 *
 */
object Matchmaker {

    val port = Constants.SERVER_PORT

    var nextRoom = 0L

    val serverSocket = ServerSocket(port)

    val pendingSockets = mutableListOf<Socket>()
    val rooms = mutableListOf<GameRoom>()

    fun acceptSockets() {
        println("accepting sockets on port $port")
        while (true) {
            val sock = serverSocket.accept()
            pendingSockets.add(sock)
            println("A dude at ${sock.inetAddress} connected, ${pendingSockets.size} dudes now")

            if (pendingSockets.size >= 2) {
                val room = GameRoom(pendingSockets.toList(), nextRoom++)
                room.start()
                rooms.add(room)
                pendingSockets.clear()
                println("created room $room")
            }
        }
    }

}

fun main(args: Array<String>) {

    Thread(Runnable { Matchmaker.acceptSockets()}).start()
    Logger.getGlobal().level = Level.ALL

    Thread.sleep(2000)

    println("creating clients")
    val clients = mutableListOf<Client>()
    for (clientId in 0..1) {
        val socket = Socket("localhost", Constants.SERVER_PORT)
        val client = Client(socket, "asdf")
        client.start()
        println("client $clientId started")
        clients.add(client)
    }
    Thread.sleep(1000)
    println("requesting from clients")
    clients.forEach {
        println("requesting")
        //println(it(0))
    }

}