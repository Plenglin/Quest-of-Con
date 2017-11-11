package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Input
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.render.ShadeSet
import ktx.app.KtxInputAdapter


object PawnActionManager {

    private var primaryShadeSet: ShadeSet = ShadeSet(emptySet())

    private var hoveringShadeSet: ShadeSet = ShadeSet(emptySet())
    var movementSquares: Map<WorldCoords, Int> = mapOf()
    var attackSquares = setOf<WorldCoords>()

    var state = PawnActionState.NONE
    var pawn: Pawn? = null

    fun beginMoving(pawn: Pawn) {
        UI.pawnTooltip.isVisible = true
        if (pawn != this.pawn || state != PawnActionState.MOVE) {
            cleanAction()
            this.pawn = pawn
            movementSquares = pawn.getMovableSquares()
            primaryShadeSet = ShadeSet(
                    movementSquares.keys,
                    mode = ShadeSet.SHADE,
                    shading = Constants.movementColor
            )
            UI.shadeSets.add(primaryShadeSet)
            state = PawnActionState.MOVE
        }
    }

    fun attemptFinishMoving(coords: WorldCoords) {
        val pawn = pawn!!
        UI.targetPlayerInterface.movePawn(pawn.id, coords, {
            if (it) cleanAction()
        })
    }

    fun beginAttacking(pawn: Pawn) {
        UI.pawnTooltip.isVisible = true
        if (pawn != this.pawn || state != PawnActionState.ATTACK) {
            cleanAction()
            this.pawn = pawn
            attackSquares = pawn.getAttackableSquares()
            primaryShadeSet = ShadeSet(
                    attackSquares,
                    mode = ShadeSet.SHADE or ShadeSet.OUTLINE,
                    shading = Constants.attackColor
            )
            UI.shadeSets.add(primaryShadeSet)
            state = PawnActionState.ATTACK
        }
    }

    fun attemptFinishAttacking(coords: WorldCoords) {
        val pawn = pawn!!
        UI.targetPlayerInterface.attackPawn(pawn.id, coords, {
            if (it) {
                cleanAction()
            }
        })
    }

    fun setTargetingRadius(coords: WorldCoords) {
        UI.shadeSets.remove(hoveringShadeSet)
        hoveringShadeSet = ShadeSet(
                PawnActionManager.pawn!!.getTargetingRadius(coords),
                mode = ShadeSet.INNER_LINES,
                shading = Constants.attackColor,
                lines = Constants.attackColor
        )
        UI.shadeSets.add(hoveringShadeSet)
    }

    fun cleanAction() {
        this.pawn = null
        state = PawnActionState.NONE
        UI.shadeSets.remove(hoveringShadeSet)
        UI.shadeSets.remove(primaryShadeSet)
        UI.pawnTooltip.isVisible = false
    }

}


enum class PawnActionState {
    NONE, MOVE, ATTACK
}

object PawnActionInputProcessor : KtxInputAdapter {

    override fun keyDown(keycode: Int): Boolean {
        val pawn = GridSelectionInputManager.selection?.tile?.pawn ?: return false
        if (pawn.team != UI.targetPlayerInterface.thisTeam || pawn.ap <= 0) {
            return false
        }
        when (keycode) {
            Input.Keys.Q -> {  // Attack
                if (pawn.attacksRemaining > 0 && pawn.maxAp > 0) {
                    PawnActionManager.beginAttacking(pawn)
                }
            }
            Input.Keys.E -> {  // Move
                if (pawn.maxAp > 0) {
                    PawnActionManager.beginMoving(pawn)
                }
            }
            Input.Keys.ESCAPE -> {  // Stop what you're doing!
                PawnActionManager.cleanAction()
                return false
            }
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button != Input.Buttons.LEFT || PawnActionManager.state == PawnActionState.NONE) {
            return false
        }
        val hovering = GridSelectionInputManager.hovering ?: return false
        when (PawnActionManager.state) {
            PawnActionState.MOVE -> {
                PawnActionManager.attemptFinishMoving(hovering)
                return true
            }
            PawnActionState.ATTACK -> {
                PawnActionManager.attemptFinishAttacking(hovering)
                return true
            }
            else -> return false
        }
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (PawnActionManager.state == PawnActionState.ATTACK) {
            val hovering = GridSelectionInputManager.hovering
            if (hovering != null && PawnActionManager.attackSquares.contains(hovering)) {
                PawnActionManager.setTargetingRadius(hovering)
            }
        }
        return false
    }
}