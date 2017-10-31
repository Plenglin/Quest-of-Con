package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.plenglin.questofcon.game.unit.Pawn


class PawnInfoPanel(skin: Skin) : Table(skin) {

    var target: Pawn? = null
    val typeLabel: Label = Label("", skin)
    val movementLabel: Label = Label("", skin)

    init {
        add(Label("Type", skin))
        add(typeLabel)
        row()
        add(Label("MP", skin))
        add(movementLabel)
    }

}

class RadialMenu : Actor() {

}