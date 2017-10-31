package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport


object UI {

    val skin = Skin(Gdx.files.internal("skin/default/skin/uiskin.skin"))

    val viewport: Viewport = ScreenViewport()

    val stage: Stage = Stage(viewport)

    lateinit var tileInfo: TileInfoPanel
    lateinit var radialMenu: RadialMenu

    fun generateUI() {
        stage.clear()

        tileInfo = TileInfoPanel(skin)
        tileInfo.debug = true
        tileInfo.isVisible = false
        tileInfo.width = 200f
        stage.addActor(tileInfo)

        radialMenu = RadialMenu(skin, 100f, 50f)
        stage.addActor(radialMenu)
    }

    fun update(delta: Float) {
        stage.act(delta)
    }

    fun draw() {
        tileInfo.setPosition(10f, UI.viewport.screenHeight - tileInfo.height - 10)

        stage.draw()
    }

    fun dispose() {
        stage.dispose()
        skin.dispose()
    }

}