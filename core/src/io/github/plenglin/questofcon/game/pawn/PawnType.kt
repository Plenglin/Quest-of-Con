package io.github.plenglin.questofcon.game.pawn

import io.github.plenglin.questofcon.game.BuildableType
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.util.Registerable

class PawnType(_name: String) : Registerable, BuildableType<Pawn> {

    override fun buildAt(coords: WorldCoords): Pawn {
        val pawn = Pawn()
        return pawn
    }

    override var id: Long = 0L
    override val name: String = _name

    var displayName = ""

    var type = PawnClass.INFANTRY

    var maxRange: Int = 1
    var minRange: Int = 0

    var targetRadius: Int = 0
    var damage = 0

    var baseHp = 1
    var maxAtks = 0
    var maxAp = 0

    var cost = 0
    var upkeep = 0

    var baseAtk = 0

    var aquatic = false
    var terrestrial = true

    var canBuild: List<BuildableType<Any>> = emptyList()

}

enum class PawnClass {
    INFANTRY, VEHICLE, MECH, SHIP
}