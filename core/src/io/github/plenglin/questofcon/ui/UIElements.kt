package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import ktx.scene2d.*


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
            units.forEach {
                add(Label(it.name.capitalize(), skin))
                add(Label("$${it.cost}", skin))
                add(TextButton("Spawn", skin).apply {
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            println("spawning ${it.name}")
                            val pawn = it.createPawnAt(team, worldCoords)
                            pawn.apRemaining = 0
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

class GameStateInfoController(val gameState: GameState) {

    lateinit var currentTeamLabel: Label
    val window: KWindow

    init {
        window = window("Game Info") {
            table {
                currentTeamLabel = label("")
                textButton("Next Turn").addListener(
                        object : ChangeListener() {
                            override fun changed(event: ChangeEvent?, actor: Actor?) {
                                gameState.nextTurn()
                                updateData()
                            }
                        }
                )
                //pack()
            }
            //pack()
        }
    }

    fun updateData() {
        val team = gameState.getCurrentTeam()
        currentTeamLabel.setText("${team.name}'s turn")
        val background = Pixmap(currentTeamLabel.width.toInt(), currentTeamLabel.height.toInt(), Pixmap.Format.RGBA8888)
        background.setColor(team.color)
        background.fill()
        currentTeamLabel.style.background = Image(Texture(background)).drawable
        background.dispose()
    }

}