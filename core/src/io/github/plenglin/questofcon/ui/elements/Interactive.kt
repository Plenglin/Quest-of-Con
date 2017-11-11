package io.github.plenglin.questofcon.ui.elements

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.plenglin.questofcon.game.grid.WorldCoords


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
