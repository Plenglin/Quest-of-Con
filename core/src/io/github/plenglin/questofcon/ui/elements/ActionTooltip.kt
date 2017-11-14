package io.github.plenglin.questofcon.ui.elements

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.plenglin.questofcon.ui.GridSelectionInputManager
import io.github.plenglin.questofcon.ui.PawnActionManager
import io.github.plenglin.questofcon.ui.PawnActionState

class ActionTooltip(skin: Skin) : Table(skin) {

    val a: Label = Label("", skin)
    val b: Label = Label("", skin)
    val c: Label = Label("", skin)
    val d: Label = Label("", skin)

    init {
        add(a).width(100f).left()
        add(b).right()
        row()
        add(c).left()
        add(d).right()
    }

    fun updateData() {
        val thePawn = PawnActionManager.pawn ?: return
        when (PawnActionManager.state) {
            PawnActionState.NONE -> this.isVisible = false
            PawnActionState.MOVE -> {
                val hov = GridSelectionInputManager.hovering
                if (hov != null) {
                    val cost = PawnActionManager.movementSquares[hov]
                    if (cost != null) {
                        isVisible = true
                        a.setText(hov.tile!!.biome.name.capitalize())
                        b.setText("$cost")
                        c.setText("Actions")
                        d.setText("${thePawn.ap} -> ${thePawn.ap - cost}")
                    } else {
                        this.isVisible = false
                    }
                }
            }
            PawnActionState.ATTACK -> {
                val target = GridSelectionInputManager.hovering
                if (target != null) {
                    isVisible = true
                    val ptarget = target.tile!!.pawn
                    val building = target.tile.building
                    val damage = thePawn.damageTo(target)
                    a.setText(ptarget?.displayName ?: "")
                    b.setText(if (ptarget != null) "${ptarget.health} -> ${ptarget.health - damage}" else "")
                    c.setText(building?.displayName ?: "")
                    d.setText(if (building != null) "${building.health} -> ${building.health - damage}" else "")
                } else {
                    isVisible = false
                }
            }
        }
        pack()
    }

}