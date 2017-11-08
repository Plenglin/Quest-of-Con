package io.github.plenglin.questofcon.server

import java.io.Serializable


enum class ClientActions : Serializable {
    READY, TALK, MOVE, ATTACK
}

enum class ServerActions : Serializable {
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
data class ServerAction(val action: ServerActions, val data: Serializable? = null) : Serializable

data class ClientRequest(val type: ClientRequestType, val key: Long) : Serializable
data class ServerResponse(val type: ClientRequestType, val data: Serializable, val responseTo: Long, val error: Int = ServerResponseError.OK) : Serializable

data class DataInitial(val teams: List<DataTeam>) : Serializable

data class DataPosition(val i: Int, val j: Int) : Serializable
data class DataTeam(val name: String, val id: Long, val color: Int) : Serializable
data class DataPawn(val id: Long, val team: Long, val type: Long, val pos: DataPosition) : Serializable
data class DataBuilding(val id: Long, val team: Long, val type: Long, val pos: DataPosition) : Serializable

data class DataPawnMovement(val unit: Long, val to: DataPosition) : Serializable
data class DataPawnAttack(val unit: Long, val pos: DataPosition) : Serializable