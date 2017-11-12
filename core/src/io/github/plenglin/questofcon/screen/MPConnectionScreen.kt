package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const
import io.github.plenglin.questofcon.*
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.net.Client
import io.github.plenglin.questofcon.ui.UI
import ktx.app.KtxScreen
import ktx.scene2d.*
import java.net.Socket


object MPConnectionScreen : KtxScreen {

    val viewport = ScreenViewport()
    val stage = Stage()

    var screenToSet: Screen? = null

    override fun show() {
        screenToSet = null

        GameData.buildings.forEach {
            println("${it.id}: ${it.name}")
        }
        Scene2DSkin.defaultSkin = UI.skin
        stage.addActor(table {
            val ip = textField("localhost")
            //val port = textField("")
            button {
                label("Connect")
            }.center().addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    println("starting client")
                    val sock = Socket(ip.text, Constants.SERVER_PORT)
                    val client = Client(sock, Config.name)
                    client.initialization.addListener {
                        println("Changing screen!")
                        screenToSet = MPGameScreen(client)
                    }
                    client.start()
                }
            })

            pack()
        }.apply {
            setPosition(400f, 400f)
        })
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        if (screenToSet != null) {
            QuestOfCon.screen = screenToSet
        }
        Gdx.gl20.glClearColor(0f, 0.5f, 0.5f, 1f)
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

}