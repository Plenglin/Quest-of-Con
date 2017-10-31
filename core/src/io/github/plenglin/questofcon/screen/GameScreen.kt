package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.plenglin.questofcon.game.Game
import io.github.plenglin.questofcon.game.render.WorldRenderer
import ktx.app.KtxScreen

/**
 *
 */
object GameScreen : KtxScreen {

    val gridCam = OrthographicCamera()
    lateinit var batch: SpriteBatch
    lateinit var shape: ShapeRenderer
    lateinit var worldRenderer: WorldRenderer

    lateinit var uiStage: Stage
    val uiViewport: Viewport = ScreenViewport()

    lateinit var game: Game

    override fun show() {
        batch = SpriteBatch()
        uiStage = Stage()
        uiStage.viewport = uiViewport
        shape = ShapeRenderer()
        game = Game()

        worldRenderer = WorldRenderer(game.world)
    }

    override fun render(delta: Float) {

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl20.glClearColor(0f, 0f, 0f ,1f)

        gridCam.zoom = 1/32f
        gridCam.position.set(0f, 0f, 0f)
        gridCam.update()

        worldRenderer.shape.projectionMatrix = gridCam.combined
        worldRenderer.render(true)

    }

    override fun dispose() {
        batch.dispose()
        shape.dispose()
        uiStage.dispose()
    }

    override fun resize(width: Int, height: Int) {
        gridCam.setToOrtho(false, width.toFloat(), height.toFloat())
    }

}