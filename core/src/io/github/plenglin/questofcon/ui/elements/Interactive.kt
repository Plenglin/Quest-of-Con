package io.github.plenglin.questofcon.ui.elements

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.UI


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

class ChatLog(skin: Skin) : Window("Chat Log", skin) {

    val playerInterface get() = UI.targetPlayerInterface

    val chatLog: Label
    val entryField: TextField
    var chatBuffer: String = ""

    init {
        entryField = TextField("", skin)
        /*entryField.setFocusTraversal(false);
        entryField.setTextFieldListener { _, c ->
            if (c == '\n') send()
        }*/

        chatLog = Label("", skin)

        val container = Container(chatLog).prefSize(200f, 150f).bottom()
        val scroll = ScrollPane(container, skin)
        scroll.setFadeScrollBars(false)
        scroll.setFlickScroll(false)

        add(scroll).colspan(2)
        row()
        add(entryField).bottom().left()
        add(TextButton("Send", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) = send()
            })
        }).bottom()
        //pack()
        width = 250f
        height = 200f

        playerInterface.chatUpdate.addListener {
            val team = playerInterface.teams[it.from]!!
            println("received msg ${it.text}")
            chatBuffer += "\n<${team.name}> ${it.text}"
            chatLog.setText(chatBuffer)
            container.pack()
            scroll.layout()
            scroll.scrollTo(0f, 0f, 0f, 0f)
        }

    }

    fun send() {
        playerInterface.sendChat(entryField.text)
        entryField.text = ""
    }

}