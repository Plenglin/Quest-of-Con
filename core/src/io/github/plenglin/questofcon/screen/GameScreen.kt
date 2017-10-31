package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.render.WorldRenderer
import io.github.plenglin.questofcon.game.ui.UI
import io.github.plenglin.questofcon.game.ui.MapMovement
import ktx.app.KtxScreen

/**
 *
 */
object GameScreen : KtxScreen {

    val gridCam = OrthographicCamera()
    lateinit var batch: SpriteBatch

    lateinit var worldRenderer: WorldRenderer

    lateinit var mapMovement: MapMovement

    lateinit var gameState: GameState

    override fun show() {
        batch = SpriteBatch()
        gameState = GameState()

        mapMovement = MapMovement(gridCam)

        worldRenderer = WorldRenderer(gameState.world)
        Gdx.input.inputProcessor = InputMultiplexer(UI.stage, mapMovement)

        gridCam.zoom = 1/32f
        gridCam.position.set(0f, 0f, 0f)

        UI.generateUI()
    }

    override fun render(delta: Float) {

        UI.update(delta)
        mapMovement.update(delta)
        gridCam.update()

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl20.glClearColor(0f, 0f, 0f ,1f)

        worldRenderer.shape.projectionMatrix = gridCam.combined
        worldRenderer.render(true)

        UI.draw()

    }

    override fun dispose() {
        batch.dispose()
        UI.dispose()
    }

    override fun resize(width: Int, height: Int) {
        gridCam.setToOrtho(false, width.toFloat(), height.toFloat())
    }

}
