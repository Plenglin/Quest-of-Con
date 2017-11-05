package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.game.pawn.PawnCreator


class TileInfoPanel(skin: Skin) : Table(skin) {

    var target: WorldCoords? = null
        set(value) {
            field = value
            updateData()
        }

    val titleLabel = Label("", skin)
    val pawn = PropertiesTable(skin)
    val building = PropertiesTable(skin)

    init {
        add(titleLabel).colspan(2)
        row()
        add(Label("Unit", skin)).center().pad(10f)
        add(pawn).top().left().expandX().pad(5f)
        row()
        add(Label("Building", skin)).center().pad(10f)
        add(building).top().left().expandX().pad(5f)
        pad(5f)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        top().left()

        super.draw(batch, parentAlpha)
    }

    fun updateData() {
        val coord = target
        if (coord?.tile != null) {
            titleLabel.setText("${coord.tile.terrain.name.capitalize()} at ${coord.i}, ${coord.j} (Cost: ${coord.tile.terrain.movementCost})")

            val pawn = coord.tile.pawn
            this.pawn.data = pawn?.getProperties() ?: emptyMap()

            val building = coord.tile.building
            this.building.data = building?.getProperties() ?: emptyMap()
        }

        pawn.updateData()
        building.updateData()
        pack()
    }

}

class PropertiesTable(skin: Skin) : Table(skin) {

    var data: Map<String, Any> = mapOf()

    fun updateData() {
        clearChildren()
        if (data.isNotEmpty()) {
            data.forEach { k, v ->
                add(Label(k.capitalize(), skin)).left().prefWidth(100f)
                add(Label(v.toString(), skin)).right()
                row()
            }
        } else {
            add(Label("none", skin)).colspan(2).center()
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        top().left()
        super.draw(batch, parentAlpha)
    }

}

class RadialMenu(val skin: Skin, var radiusX: Float, var radiusY: Float) : Group() {

    var active = false
    var items = listOf<Selectable>()
    var deadzoneX = 0f
    var deadzoneY = 0f

    fun updateUI() {
        clearChildren()
        for (i in 0 until items.size) {
            val sel = items[i]

            val angle = i.toFloat() / items.size * 2 * Math.PI

            val button = Label(sel.title, skin, "radial-menu-item")
            //button.pad(-5f)
            button.setPosition(
                    radiusX * Math.sin(angle).toFloat() - button.width / 2,
                    radiusY * Math.cos(angle).toFloat() - button.height / 2)

            button.isVisible = true
            button.debug = true
            addActor(button)
        }
    }

    fun getSelected(xOff: Double, yOff: Double): Selectable? {
        if (items.isEmpty() || xOff * xOff / deadzoneX / deadzoneX + yOff * yOff / deadzoneY / deadzoneY < 1) {  // In deadzone ellipse?
            return null
        }
        val bearing = (450 - Math.toDegrees(Math.atan2(yOff, xOff))) % 360
        for (i in items.indices) {
            val rot = 360 * (i + 0.5) / items.size
            if (bearing <= rot) {
                return items[i]
            }
        }
        return items[0]
    }

}

data class Selectable(val title: String, val onSelected: (WorldCoords) -> Unit) {

    override fun toString(): String {
        return "Selectable($title)"
    }
}

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
                            team.money -= pawn.cost
                            val newPawn = pawn.createPawnAt(team, worldCoords)
                            newPawn.apRemaining = 0
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

class BuildingSpawningDialog(val team: Team, skin: Skin, val worldCoords: WorldCoords) : Dialog("Spawn", skin) {

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
                            team.money -= bldg.cost
                            val building = bldg.createBuildingAt(team, worldCoords)
                            building.enabled = false
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

class GameStateInfoController(val gameState: GameState, skin: Skin) : Window("Status", skin) {

    val currentTeamLabel: Label
    val moneyLabel: Label
    val ecoLabel: Label

    init {
        currentTeamLabel = Label("", skin)
        moneyLabel = Label("", skin)
        ecoLabel = Label("", skin)

        add(currentTeamLabel)
        add(TextButton("Next Turn", skin).apply {
            addListener(
                    object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            gameState.nextTurn()
                            updateData()
                        }
                    }
            )
        })
        row()
        add(moneyLabel)
        add(ecoLabel)
        pack()
    }

    fun updateData() {
        val team = gameState.getCurrentTeam()
        currentTeamLabel.setText("${team.name}'s turn")
        moneyLabel.setText("$${team.money}")
        ecoLabel.setText("+$${team.getMoneyPerTurn()}")
        pack()
    }

}

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
        val pawn = PawnActionManager.pawn ?: return
        when (PawnActionManager.state) {
            PawnActionState.NONE -> this.isVisible = false
            PawnActionState.MOVE -> {
                val hov = GridSelectionInputManager.hovering
                if (hov != null) {
                    val cost = PawnActionManager.movementSquares[hov]
                    if (cost != null) {
                        isVisible = true
                        a.setText(hov.tile!!.terrain.name.capitalize())
                        b.setText("$cost")
                        c.setText("Actions")
                        d.setText("${pawn.apRemaining} -> ${pawn.apRemaining - cost}")
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
                    val damage = pawn.damageTo(target)
                    a.setText(ptarget?.name ?: "")
                    b.setText(if (ptarget != null) "${ptarget.health} -> ${ptarget.health - damage}" else "")
                    c.setText(building?.name ?: "")
                    d.setText(if (building != null) "${building.health} -> ${building.health - damage}" else "")
                } else {
                    isVisible = false
                }
            }
        }
        pack()
    }

}