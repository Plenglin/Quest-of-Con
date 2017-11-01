package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.screen.GameScreen


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
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        pad(5f)
        top().left()

        super.draw(batch, parentAlpha)
    }

    fun updateData() {
        val coord = target
        if (coord?.tile != null) {
            titleLabel.setText("${coord.tile.terrain.name.capitalize()} at ${coord.i}, ${coord.j}")

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
        if (data.size > 0) {
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
    var selectables = listOf<Selectable>()

    private var selected: RadialMenuItem? = null
    private val items = mutableListOf<RadialMenuItem>()

    fun updateUI() {

        clearChildren()
        for (i in 0 until selectables.size) {
            val sel = selectables[i]

            val angle = i.toFloat() / selectables.size * 2 * Math.PI

            val button = TextButton(sel.title, skin, "radial-menu-item")
            //button.pad(-5f)
            val listener = ClickListener()
            button.setPosition(
                    radiusX * Math.sin(angle).toFloat() - button.width / 2,
                    radiusY * Math.cos(angle).toFloat() - button.height / 2)

            println(radiusX * Math.sin(angle).toFloat() - button.width / 2)

            button.addListener(listener)
            button.isVisible = true
            button.debug = true
            addActor(button)
            items.add(RadialMenuItem(button, sel, listener))
        }
    }

    override fun act(delta: Float) {
        if (active) {
            selected = null
            items.forEach {
                if (it.clickListener.isPressed) {
                    selected = it
                }
            }
            if (selected != null) {
                println("selected $selected")
                this.selected!!.selectable.onSelected(this.x, this.y)
                this.active = false
                this.isVisible = false
            }
        }
        super.act(delta)
    }

}

private data class RadialMenuItem(val label: TextButton, val selectable: Selectable, val clickListener: ClickListener)

abstract class Selectable(val title: String) {

    /**
     * Called when this selectable is selected. Parameters are at the center of the radial menu.
     */
    abstract fun onSelected(x: Float, y: Float)

    override fun toString(): String {
        return title
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
            add(Label("Spawn", skin))
            row()
            units.forEach { pawn ->
                add(Label(pawn.name.capitalize(), skin))
                add(Label("$${pawn.cost}", skin))
                add(TextButton("Spawn", skin).apply {

                    this.isDisabled = pawn.cost > team.money

                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            println("spawning ${pawn.name}")
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
        println(buildings)
        contentTable.apply {
            add(Label("Type", skin))
            add(Label("Cost", skin))
            add(Label("Build", skin))
            row()
            buildings.forEach { bldg ->
                add(Label(bldg.name.capitalize(), skin))
                add(Label("$${bldg.cost}", skin))
                add(TextButton("Build", skin).apply {
                    isDisabled = bldg.cost > team.money

                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            println("spawning ${bldg.name}")
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

    companion object : Selectable("Build") {
        override fun onSelected(x: Float, y: Float) {
            BuildingSpawningDialog(
                    GameScreen.gameState.getCurrentTeam(),
                    UI.skin,
                    GameScreen.gridSelection.selection!!
            ).show(UI.stage)
        }

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
        /*
        val background = Pixmap(currentTeamLabel.width.toInt(), currentTeamLabel.height.toInt(), Pixmap.Format.RGBA8888)
        background.setColor(team.color)
        background.fill()
        currentTeamLabel.style.background = Image(Texture(background)).drawable
        background.dispose()*/
    }

}