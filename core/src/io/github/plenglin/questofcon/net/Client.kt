package io.github.plenglin.questofcon.net

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket
import java.util.logging.Logger

/**
 *
 */
class Client(val socket: Socket) : Thread("Client-$socket") {

    val logger = Logger.getLogger(this.name)

    //CHRIS WAS HERE
    //lateinit var socket: Socket
    lateinit var input: ObjectInputStream
    lateinit var output: ObjectOutputStream

    var onChangeTurn: (DataTeam) -> Unit = {}

    private var nextTransmissionId = 0L
    //private val transmissionQueue = Queue<Transmission>()

    private val responseListeners = mutableMapOf<Long, (ServerResponse) -> Unit>()

    override fun run() {
        println("starting ${this.name}")
        input = ObjectInputStream(socket.getInputStream())
        output = ObjectOutputStream(socket.getOutputStream())

        println("beginning")
        while (true) {
            val trans = input.readObject() as Transmission
            val data = trans.payload
            when (data) {
                is ServerAction -> onActionReceived(data)
                is ServerResponse -> onResponseReceived(data)
            }
        }
    }

    private fun onActionReceived(action: ServerAction) {
        logger.fine("received action $action")
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

    /**
     * Request something.
     */
    fun request(type: ClientRequestType, key: Long, onResponse: (ServerResponse) -> Unit = {}) {
        val id = send(ClientRequest(type, key))
        responseListeners[id] = onResponse
    }

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
    }

    fun getNextId(): Long {
        synchronized (nextTransmissionId) {
            return nextTransmissionId++
        }
    }


}