package io.github.plenglin.questofcon.ui

import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.render.ShadeSet
import io.github.plenglin.questofcon.screen.GameScreen


object PawnActionManager {

    private var primaryShadeSet: ShadeSet = ShadeSet(emptySet())

    private var hoveringShadeSet: ShadeSet = ShadeSet(emptySet())
    var movementSquares: Map<WorldCoords, Int> = mapOf()
    var attackSquares = setOf<WorldCoords>()

    var state = State.NONE
    var pawn: Pawn? = null

    fun beginMoving(pawn: Pawn) {
        if (pawn != this.pawn || state != State.MOVE) {
            cleanAction()
            this.pawn = pawn
            movementSquares = pawn.getMovableSquares()
            primaryShadeSet = ShadeSet(movementSquares.keys)
            GameScreen.shadeSets.add(primaryShadeSet)
            state = State.MOVE
        }
    }

    fun attemptFinishMoving(coords: WorldCoords): Boolean {
        val pawn = pawn!!
        if (pawn.moveTo(coords, movementSquares)) {
            cleanAction()
            return true
        } else {
            return false
        }
    }

    fun beginAttacking(pawn: Pawn) {
        if (pawn != this.pawn || state != State.ATTACK) {
            cleanAction()
            this.pawn = pawn
            attackSquares = pawn.getAttackableSquares()
            primaryShadeSet = ShadeSet(movementSquares.keys)
            GameScreen.shadeSets.add(primaryShadeSet)
            state = State.ATTACK
        }
    }

    fun finishAttacking(coords: WorldCoords): Boolean {
        val pawn = pawn!!
        if (attackSquares.contains(coords) && pawn.attemptAttack(coords)) {
            cleanAction()
            return true
        } else {
            return false
        }
    }

    fun cleanAction() {
        this.pawn = null
        state = State.NONE
        GameScreen.shadeSets.remove(hoveringShadeSet)
        GameScreen.shadeSets.remove(primaryShadeSet)
    }

    enum class State {
        NONE, MOVE, ATTACK
    }

}