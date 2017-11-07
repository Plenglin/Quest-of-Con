package io.github.plenglin.questofcon.server.data

import java.io.Serializable


enum class ClientActions : Serializable {
    TALK, MOVE, ATTACK
}

data class DataAction(val action: ClientActions, val data: Serializable) : Serializable

data class DataPosition(val i: Int, val j: Int) : Serializable
data class DataTeam(val name: String, val id: Long, val color: Int) : Serializable
data class DataInitial(val teams: List<DataTeam>) : Serializable
data class DataPawnMovement(val unit: Long, val to: DataPosition) : Serializable
data class DataPawnAttack(val unit: Long, val pos: DataPosition) : Serializable
data class DataPawn(val id: Long, val team: Long, val type: Long, val pos: DataPosition) : Serializable
data class DataBuilding(val id: Long, val team: Long, val type: Long, val pos: DataPosition) : Serializable
