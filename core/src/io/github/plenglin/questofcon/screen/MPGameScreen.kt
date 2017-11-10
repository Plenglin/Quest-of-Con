package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.net.Client
import io.github.plenglin.questofcon.render.WorldRenderer
import ktx.app.KtxScreen

/**
 *
 */
object MPGameScreen : KtxScreen {

    lateinit var client: Client
    lateinit var gameState: GameState
    lateinit var renderer: WorldRenderer

    val gridCam = OrthographicCamera()

    fun initializeWith(client: Client) {
        this.client = client
    }

    override fun show() {
        gameState = GameState(client.initialResponse.teams.map { Team(it.name, Color(it.color)) })
        renderer = WorldRenderer(gameState.world)
    }

    override fun render(delta: Float) {

        gridCam.update()
        renderer.batch.projectionMatrix = gridCam.combined
        renderer.shape.projectionMatrix = gridCam.combined

        Gdx.gl30.glClearColor(0f, 0f, 0f ,1f)
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)

        renderer.render()
    }

    override fun resize(width: Int, height: Int) {
        gridCam.setToOrtho(true, width.toFloat(), height.toFloat())
    }

}