package io.github.plenglin.questofcon.net

import java.io.Serializable
import java.util.*


enum class ClientActions : Serializable {
    READY, TALK, MOVE, ATTACK
}

enum class ServerEventTypes : Serializable {
    WORLD_STATE, PAWN_MOVEMENT, PAWN_ATTACK, PAWN_DEATH, TERRAIN_CHANGE
}

enum class ClientRequestType {
    TEAM, PAWN, TILE, BUILDING
}

object ServerResponseError {
    val OK = 0
    val ID_DOES_NOT_EXIST = 1
    val DATA_ERROR = 2
}

data class Transmission(val id: Long, val payload: Serializable) : Serializable

data class ClientAction(val action: ClientActions, val data: Serializable? = null) : Serializable
data class ServerEvent(val action: ServerEventTypes, val data: Serializable? = null) : Serializable

data class ClientRequest(val type: ClientRequestType, val key: Long) : Serializable
data class ServerResponse(val type: ClientRequestType, val data: Serializable?, val responseTo: Long, val error: Int = ServerResponseError.OK) : Serializable

data class DataInitialClientData(val name: String) : Serializable
data class DataInitialResponse(val yourId: Long, val teams: List<DataTeam>, val world: DataWorldState) : Serializable

data class DataWorldState(val grid: Array<Array<DataTile>>) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataWorldState

        if (!Arrays.equals(grid, other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(grid)
    }
}
data class DataTile(val biome: Long, val elevation: Int) : Serializable

data class DataPosition(val i: Int, val j: Int) : Serializable
data class DataTeam(val name: String, val id: Long, val color: Int) : Serializable
data class DataPawn(val id: Long, val team: Long, val type: Long, val pos: DataPosition) : Serializable
data class DataBuilding(val id: Long, val team: Long, val type: Long, val pos: DataPosition) : Serializable

data class DataPawnMovement(val unit: Long, val to: DataPosition) : Serializable
data class DataPawnAttack(val unit: Long, val pos: DataPosition) : Serializable