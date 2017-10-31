package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import io.github.plenglin.questofcon.game.grid.WorldCoords
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
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        pad(5f)
        top().left()
        this.height = 200f

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
    }

}

class PropertiesTable(skin: Skin) : Table(skin) {

    var data: Map<String, Any> = mapOf()

    fun updateData() {
        clearChildren()
        if (data.size > 0) {
            data.forEach { k, v ->
                add(Label(k.capitalize(), skin)).left().prefWidth(999f)
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
        setScale(1f)
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
        setPosition((UI.viewport.screenWidth / 2).toFloat(), (UI.viewport.screenHeight / 2).toFloat(), Align.center)
    }

    override fun result(obj: Any?) {
        when (obj) {
            1 -> onConfirm()
            2 -> remove()
        }
    }

}

class UnitSpawningDialog(val units: List<PawnCreator>, skin: Skin) : Dialog("Spawn", skin) {

    init {/*
        table {
            label("Type")
            label("Cost")
            units.forEach {
                label(it.name)
                label("$$it.cost")
                button("Spawn", it)
                row()
            }
        }*/
    }

    override fun result(btn: Any?) {
        val pawn = btn as PawnCreator
        println("Spawn ${pawn.name}")
        remove()
    }
}