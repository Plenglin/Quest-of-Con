package io.github.plenglin.questofcon.game


sealed class Event

data class PawnChangeEvent(val id: Long) : Event()
data class BuildingChangeEvent(val id: Long) : Event()
data class WorldChangeEvent(val id: Long) : Event()
