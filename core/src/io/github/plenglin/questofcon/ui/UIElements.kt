package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import io.github.plenglin.questofcon.game.grid.WorldCoords


class TileInfoPanel(skin: Skin) : Table(skin) {

    var target: WorldCoords? = null
        set(value) {
            field = value

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

class RadialMenu(skin: Skin, val radiusX: Float, val radiusY: Float, vararg selectables: Selectable) : Group() {

    var active = false
    var selectables = selectables.toList()

    private var selected: RadialMenuItem? = null
    private val items = mutableListOf<RadialMenuItem>()

    init {
        setScale(1f)
        for (i in 0 until selectables.size) {
            val sel = selectables[i]

            val label = Label(sel.title, skin)

            val angle = i.toFloat() / selectables.size * 2 * Math.PI

            val listener = ClickListener()

            //label.setSize(100f, 100f)
            label.setPosition(
                    radiusX * Math.sin(angle).toFloat() - label.width / 2,
                    radiusY * Math.cos(angle).toFloat() - label.height / 2)

            println(radiusX * Math.sin(angle).toFloat() - label.width / 2)

            label.addListener(listener)
            label.isVisible = true
            label.debug = true
            addActor(label)
            items.add(RadialMenuItem(label, sel, listener))
        }
    }

    override fun act(delta: Float) {
        if (active) {
            selected = null
            items.forEach {
                if (it.clickListener.isOver) {
                    selected = it
                }
            }
            if (selected != null) {
                println("selected $selected")
                this.selected!!.selectable.onSelected()
                this.active = false
                this.isVisible = false
            }
        }
        super.act(delta)
    }

}

private data class RadialMenuItem(val label: Label, val selectable: Selectable, val clickListener: ClickListener)

abstract class Selectable(val title: String) {

    abstract fun onSelected()

}