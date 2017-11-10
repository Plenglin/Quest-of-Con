package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.render.ShadeSet
import io.github.plenglin.questofcon.screen.GameScreen
import ktx.scene2d.Scene2DSkin


object UI {

    val gridCam = OrthographicCamera()
    val skin = Skin(Gdx.files.internal("skin/default/skin/uiskin.skin"))

    val viewport: Viewport = ScreenViewport()

    val stage: Stage = Stage(viewport)

    val shadeSets = mutableListOf<ShadeSet>()

    //lateinit var targetGameState: GameState
    lateinit var targetPlayerInterface: PlayerInterface

    lateinit var infoPanel: GameStateInfoController
    lateinit var tileInfo: TileInfoPanel
    lateinit var radialMenu: RadialMenu
    lateinit var pawnTooltip: ActionTooltip

    fun generateUI() {
        stage.clear()

        Scene2DSkin.defaultSkin = skin

        tileInfo = TileInfoPanel(skin)
        tileInfo.debug = true
        tileInfo.isVisible = false
        tileInfo.width = 200f
        stage.addActor(tileInfo)

        radialMenu = RadialMenu(skin, 100f, 50f)
        radialMenu.deadzoneX = 30f
        radialMenu.deadzoneY = 15f
        stage.addActor(radialMenu)

        pawnTooltip = ActionTooltip(skin)
        stage.addActor(pawnTooltip)

        infoPanel = GameStateInfoController(GameScreen.gameState, skin)
        stage.addActor(infoPanel)
        infoPanel.updateData()
    }

    fun updateData() {
        infoPanel.updateData()
        tileInfo.updateData()
        pawnTooltip.updateData()

        pawnTooltip.setPosition(Gdx.input.x.toFloat(), viewport.screenHeight - Gdx.input.y.toFloat())
    }

    fun update(delta: Float) {
        stage.act(delta)
    }

    fun draw() {
        tileInfo.setPosition(10f, UI.viewport.screenHeight - tileInfo.height - 10)
        infoPanel.setPosition(Gdx.graphics.width - infoPanel.width, 0f)

        stage.draw()
    }

    fun dispose() {
        stage.dispose()
        skin.dispose()
    }

}