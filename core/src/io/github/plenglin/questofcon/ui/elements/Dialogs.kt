package io.github.plenglin.questofcon.ui.elements

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.ui.UI

class ConfirmationDialog(title: String, skin: Skin, val onConfirm: () -> Unit) : Dialog(title, skin) {

    init {
        text("Are you sure?")
        button("OK", 1)
        button("Cancel", 2)
        setPosition((UI.viewport.screenWidth / 2).toFloat(), (UI.viewport.screenHeight / 2).toFloat())
        pack()
    }

    override fun result(obj: Any?) {
        when (obj) {
            1 -> onConfirm()
        }
    }

}

class UnitSpawningDialog(val units: List<PawnCreator>, skin: Skin, val worldCoords: WorldCoords, val team: Team) : Dialog("Spawn", skin) {

    init {
        contentTable.apply {
            add(Label("Type", skin))
            add(Label("Cost", skin))
            row()
            units.forEach { pawn ->
                add(Label(pawn.title.capitalize(), skin))
                add(Label("$${pawn.cost}", skin))
                add(TextButton("Spawn", skin).apply {

                    this.isDisabled = pawn.cost > team.money

                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            UI.targetPlayerInterface.makePawn(worldCoords, pawn)
                            UI.updateData()
                            this@UnitSpawningDialog.hide()
                        }
                    })

                })
                row()
            }
        }
        button("Cancel")
        pack()
        setPosition((UI.viewport.screenWidth / 2).toFloat(), (UI.viewport.screenHeight / 2).toFloat())
    }

}

class BuildingSpawningDialog(skin: Skin, val worldCoords: WorldCoords) : Dialog("Spawn", skin) {

    val team = UI.targetPlayerInterface.thisTeam

    init {
        val buildings = team.getBuildable()
        contentTable.apply {
            add(Label("Type", skin))
            add(Label("Cost", skin))
            row()
            buildings.forEach { bldg ->
                add(Label(bldg.name.capitalize(), skin))
                add(Label("$${bldg.cost}", skin))
                add(TextButton("Build", skin).apply {
                    isDisabled = bldg.cost > team.money

                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            UI.targetPlayerInterface.makeBuilding(worldCoords, bldg, {})
                            UI.updateData()
                            this@BuildingSpawningDialog.hide()
                        }
                    })
                })
                row()
            }
        }
        button("Cancel")
        pack()
        setPosition((UI.viewport.screenWidth / 2).toFloat(), (UI.viewport.screenHeight / 2).toFloat())
    }

}
