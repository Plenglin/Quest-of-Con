package io.github.plenglin.questofcon.game.pawn

import io.github.plenglin.questofcon.game.Buildable
import io.github.plenglin.util.Registerable

class PawnType(_name: String) : Registerable {

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

    var canBuild: List<Buildable> = emptyList()

}

enum class PawnClass {
    INFANTRY, VEHICLE, MECH, SHIP
}