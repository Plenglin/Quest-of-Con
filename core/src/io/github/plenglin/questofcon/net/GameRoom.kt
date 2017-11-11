package io.github.plenglin.questofcon.net

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.ListenerManager
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.PawnChangeEvent
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.*
import java.io.Serializable
import java.net.Socket
import java.util.concurrent.CyclicBarrier
import java.util.logging.Logger


class GameRoom(val sockets: List<Socket>, val roomId: Long) : Thread("GameRoom-$roomId"), Iterable<SocketManager> {
    override fun iterator(): Iterator<SocketManager> {
        return clientsById.values.iterator()
    }

    val initializationFinished = ListenerManager<Unit>()

    lateinit var gameState: GameState
    val colors = mutableListOf<Color>(Color.RED, Color.GREEN, Color.BLUE)
    val barrier = CyclicBarrier(sockets.size + 1)
    val logger = Logger.getLogger(javaClass.name)

    val clientsById = mutableMapOf<Long, SocketManager>()

    override fun run() {
        logger.info("${name} starting")
        logger.info("$name connecting to clients")

        barrier.reset()

        val clients = sockets.map { SocketManager(it, this) }
        clients.forEach { it.start() }

        logger.info("$name waiting for clients to send initial message")
        barrier.await()

        logger.info("$name all clients sent info, now creating teams")

        gameState = GameState(clients.map {
            val data = it.initialTransmission
            val team = Team(data.name, colors.removeAt(0))
            logger.info("$name made team $team")
            clientsById.put(team.id, it)
            it.team = team
            team
        })

        gameState.turnChange.addListener {
            broadcastEvent(ServerEventTypes.CHANGE_TURN, it.id)
            clientsById[it.id]!!.send(DataTeamBalance(gameState.getCurrentTeam().money))
        }
        gameState.pawnChange.addListener {
            broadcastEvent(ServerEventTypes.PAWN_CHANGE, it.serialized())
        }
        gameState.buildingChange.addListener {
            broadcastEvent(ServerEventTypes.BUILDING_CHANGE, it.serialized())
        }
        gameState.worldChange.addListener {
            broadcastEvent(ServerEventTypes.TERRAIN_CHANGE, gameState.world.serialized())
        }

        logger.info("generating world")

        generateWorld()

        logger.info("sending back data")
        sendInitialServerResponse()

        initializationFinished.fire(Unit)
    }

    fun broadcastEvent(eventType: ServerEventTypes, data: Serializable? = null) {
        logger.info("bcast $eventType: $data")
        forEach { it.send(ServerEvent(eventType, data)) }
    }

    private fun sendInitialServerResponse() {
        clientsById.forEach { _, sockMan ->
            sockMan.send(DataInitialResponse(
                    sockMan.team.id,
                    clientsById.values.map { it.team.serialized() },
                    gameState.world.serialized()
            ))
        }
    }

    fun generateWorld() {
        val heightData = HeightMap(DiamondSquareHeightGenerator(3, initialOffsets = 2.0, iterativeRescale = 0.8).generate().grid).normalized
        val rainfallData = HeightMap(DiamondSquareHeightGenerator(3, initialOffsets = 2.0, iterativeRescale = 0.8).generate().grid).normalized

        println("Mapping height data to world...")
        MapToHeight(gameState.world, heightData).doHeightMap()

        println("Adding biomes...")
        BiomeGenerator(gameState.world, heightData, rainfallData).applyBiomes()

    }

    fun changeTurn() {
        gameState.nextTurn()
    }
}
