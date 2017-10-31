package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
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

class RadialMenu : Actor() {

}