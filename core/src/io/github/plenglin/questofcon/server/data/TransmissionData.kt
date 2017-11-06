package io.github.plenglin.questofcon.server.data

data class DataPosition(val i: Int, val j: Int)

data class DataTeam(val name: String, val id: Long, val color: Int)

data class DataInitial(val teams: List<DataTeam>)

data class DataPawnMovement(val unit: Long, val to: DataPosition)

data class DataPawnAttack(val unit: Long, val pos: DataPosition)

data class DataPawn(val id: Long, val team: Long, val type: Long, val pos: DataPosition)

data class DataBuilding(val id: Long, val team: Long, val type: Long, val pos: DataPosition)
