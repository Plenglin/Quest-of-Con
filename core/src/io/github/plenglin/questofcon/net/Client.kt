package io.github.plenglin.questofcon.net

import io.github.plenglin.questofcon.ListenerManager
import io.github.plenglin.questofcon.game.DummyGameState
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket
import java.util.logging.Logger

/**
 * Client-side server data sender.
 */
class Client(val socket: Socket, val playerName: String) : Thread("Client-$playerName-$socket") {

    val dummy = DummyGameState()
    val logger = Logger.getLogger(this.name)

    //CHRIS WAS HERE
    //lateinit var socket: Socket
    lateinit var input: ObjectInputStream
    lateinit var output: ObjectOutputStream

    lateinit var initialResponse: DataInitialResponse

    var onTurnChanged = ListenerManager<DataTeam>()

    /**
     * Called when the server has responded to our initial request.
     */
    var initialization = ListenerManager<Client>()

    val onServerEvent = ListenerManager<ServerEvent>()

    private var nextTransmissionId = 0L
    //private val transmissionQueue = Queue<Transmission>()

    private val responseListeners = mutableMapOf<Long, (ServerResponse) -> Unit>()

    override fun run() {
        println("starting ${this.name}")
        input = ObjectInputStream(socket.getInputStream())
        output = ObjectOutputStream(socket.getOutputStream())

        println("$name sending initial data")
        sendInitialData()

        while (true) {
            val trans = input.readObject() as Transmission
            val data = trans.payload
            when (data) {
                is DataInitialResponse -> {
                    println(data)
                    initialResponse = data
                    initialization.fire(this)
                }
                is ServerEvent -> onEventReceived(data)
                is ServerResponse -> onResponseReceived(data)
            }
        }
    }

    private fun onEventReceived(event: ServerEvent) {
        logger.info("received event $event")
        onServerEvent.fire(event)
    }

    private fun onResponseReceived(response: ServerResponse) {
        logger.info("received response $response")
        responseListeners.remove(response.responseTo)!!.invoke(response)
    }

    private fun send(data: Serializable): Long {
        val id = getNextId()
        logger.info("sending $id, $data")
        output.writeObject(Transmission(id, data))
        return id
    }

    private fun sendInitialData() {
        send(DataInitialClientData(playerName))
    }

    /**
     * Request something.
     */
    fun request(type: ClientRequestType, key: Long, onResponse: (ServerResponse) -> Unit = {}) {
        val id = send(ClientRequest(type, key))
        responseListeners[id] = onResponse
    }

    /**
     * Request something.
     */
    fun action(type: ClientActions, data: Serializable? = null, onResponse: (ServerResponse) -> Unit = {}) {
        val id = send(ClientAction(type, data))
        responseListeners[id] = onResponse
    }

    /*
    /**
     * Request something, but block the thread until that something is received.
     */
    fun requestBlocking(type: ClientRequestType, key: Long): ServerResponse {
        var received: ServerResponse? = null
        request(type, key, {
            received = it
        })
        while (received == null) {Thread.sleep(10)}
        return received!!
    }

    fun getBuildingWithId(key: Long): DataBuilding? {
        return requestBlocking(ClientRequestType.BUILDING, key).data as DataBuilding
    }

    fun getPawnWithId(key: Long): DataPawn? {
        return requestBlocking(ClientRequestType.PAWN, key).data as DataPawn
    }*/

    private fun getNextId(): Long {
        synchronized (nextTransmissionId) {
            return nextTransmissionId++
        }
    }


}