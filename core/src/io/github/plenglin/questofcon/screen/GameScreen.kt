package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen

/**
 *
 */
object GameScreen : KtxScreen {

    val gridCam = OrthographicCamera()
    lateinit var batch: SpriteBatch

    lateinit var uiStage: Stage
    val uiViewport: Viewport = ScreenViewport()

    override fun show() {
        batch = SpriteBatch()
        uiStage = Stage()
        uiStage.viewport = uiViewport
    }

    override fun render(delta: Float) {

    }

    override fun dispose() {

    }

}