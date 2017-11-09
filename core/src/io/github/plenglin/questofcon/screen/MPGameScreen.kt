package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.net.Client
import ktx.app.KtxScreen

/**
 *
 */
object MPGameScreen : KtxScreen {

    lateinit var client: Client

    fun initializeWith(client: Client) {
        this.client = client
    }

    override fun show() {
        client.start()
        client.onInitialize = {
            it.sendInitialData(System.getenv("title"))
        }
    }

}