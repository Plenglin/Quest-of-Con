package io.github.plenglin.questofcon.server

import com.beust.klaxon.JSON
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import io.github.plenglin.questofcon.server.data.ClientActions
import io.github.plenglin.questofcon.server.data.DataAction
import java.io.BufferedInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.PrintStream
import java.net.Socket
import java.util.*


class Room(val sockets: List<Socket>) : Thread() {

    val clients = sockets.map { SocketManager(it) }

    override fun run() {
        println("room of $sockets")
        clients.forEach { it.start() }
    }

}

class SocketManager(val socket: Socket) : Thread() {

    var parser = Parser()

    override fun run() {
        println("starting socket manager for $socket")
        val input = ObjectInputStream(socket.getInputStream())
        val output = ObjectOutputStream(socket.getOutputStream())

        input.use { output.use {
            println("we are in the beeme")
            while (true) {
                val data = input.readObject() as DataAction
                val rawString = println("rcv $data")

                when (data.action) {
                    ClientActions.TALK -> {
                        println(data.data as String)
                    }
                    ClientActions.MOVE -> TODO()
                    ClientActions.ATTACK -> TODO()
                }
            }
        } }
    }

}