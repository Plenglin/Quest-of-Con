package io.github.plenglin.questofcon.server

import io.github.plenglin.questofcon.server.data.DataServerAction
import io.github.plenglin.questofcon.server.data.DataServerResponse
import io.github.plenglin.questofcon.server.data.DataTeam
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

/**
 *
 */
class Client(val socket: Socket) : Thread("Client-${socket.inetAddress}") {

    val input = ObjectInputStream(socket.getInputStream())
    val output = ObjectOutputStream(socket.getOutputStream())

    var onChangeTurn: (DataTeam) -> Unit = {}

    override fun run() {
        while (socket.isConnected) {
            val data = input.readObject()
            when (data) {
                is DataServerAction -> {
                }
                is DataServerResponse -> {}
            }
        }
    }

}