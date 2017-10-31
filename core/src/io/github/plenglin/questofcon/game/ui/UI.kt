package io.github.plenglin.questofcon.game.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport


object UI {

    val viewport: Viewport = ScreenViewport()

    val stage: Stage = Stage()

    lateinit var unitInfoPanel: Table

    fun generateUI() {
        stage.viewport = viewport

        stage.clear()

    }

    fun update(delta: Float) {
        stage.act(delta)
    }

    fun draw() {
        stage.draw()
    }

    fun dispose() {
        stage.dispose()
    }

}