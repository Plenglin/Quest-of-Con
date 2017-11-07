package io.github.plenglin.questofcon.server

import com.beust.klaxon.JSON
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import java.io.BufferedInputStream
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
        val input = Scanner(BufferedInputStream(socket.getInputStream()))
        val output = PrintStream(socket.getOutputStream())
        while (true) {
            val rawString = input.nextLine()
            println("$socket -> $rawString")
            val json = parser.parse(StringBuilder(rawString)) as JsonObject
            when (json.string("action")) {
                "talk" -> {
                    output.println("OK")
                    println("$socket: ${json.string("msg")}")
                }
            }
        }
    }

}