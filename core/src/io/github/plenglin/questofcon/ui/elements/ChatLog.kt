package io.github.plenglin.questofcon.ui.elements

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import io.github.plenglin.questofcon.ui.UI

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
        chatLog.setWrap(true)
        chatLog.width = 200f

        val container = Container(chatLog).prefWidth(250f).minHeight(200f).bottom()//.size(225f, 200f)
        val scroll = ScrollPane(container, skin)
        scroll.setFadeScrollBars(false)
        scroll.setFlickScroll(false)
        container.pack()

        add(scroll).colspan(2)
        row()
        add(entryField).bottom().left()
        add(TextButton("Send", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) = send()
            })
        }).bottom()

        width = 300f
        height = 250f

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