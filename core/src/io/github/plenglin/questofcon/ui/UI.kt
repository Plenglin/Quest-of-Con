package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport


object UI {

    val skin = Skin(Gdx.files.internal("skin/default/skin/uiskin.skin"))

    val viewport: Viewport = ScreenViewport()

    val stage: Stage = Stage()

    lateinit var tileInfo: TileInfoPanel

    fun generateUI() {
        stage.viewport = viewport

        stage.clear()

        tileInfo = TileInfoPanel(skin)
        tileInfo.debug = true
        tileInfo.isVisible = false
        stage.addActor(tileInfo)
    }

    fun update(delta: Float) {
        stage.act(delta)
    }

    fun draw() {
        stage.draw()
    }

    fun dispose() {
        stage.dispose()
        skin.dispose()
    }

}