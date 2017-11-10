package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
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
    }

    override fun render(delta: Float) {
        Gdx.gl30.glClearColor(0f, 0f, 0f ,1f)
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)
    }

}