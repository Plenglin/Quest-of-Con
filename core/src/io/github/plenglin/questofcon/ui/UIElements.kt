package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import io.github.plenglin.questofcon.game.grid.WorldCoords


class TileInfoPanel(skin: Skin) : Table(skin) {

    var target: WorldCoords? = null

    val pawn = PropertiesTable(skin)
    val building = PropertiesTable(skin)

    init {

        add(pawn)
        row()
        add(building)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {

        setPosition(0f, UI.viewport.screenHeight - height)
        setSize(100f, 300f)
        top().left()
        val coord = target
        if (coord?.tile != null) {
            val pawn = coord.tile.pawn
            if (pawn != null) {
                this.pawn.data = pawn.getProperties()
            }

            val building = coord.tile.building
            if (building != null) {
                this.building.data = building.getProperties()
            }
        }

        super.draw(batch, parentAlpha)
    }

}

class PropertiesTable(skin: Skin) : Table(skin) {

    var data: Map<String, Any> = mapOf()

    override fun draw(batch: Batch?, parentAlpha: Float) {
        clearChildren()
        data.forEach { k, v ->
            add(Label(k, skin))
            add(Label(v.toString(), skin))
            row()
        }

        super.draw(batch, parentAlpha)
    }

}

class RadialMenu : Actor() {

}