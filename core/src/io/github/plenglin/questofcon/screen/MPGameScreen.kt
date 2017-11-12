package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.BuildingFactory
import io.github.plenglin.questofcon.game.grid.*
import io.github.plenglin.questofcon.interop.NetworkedPlayerInterface
import io.github.plenglin.questofcon.interop.PassAndPlayInterface
import io.github.plenglin.questofcon.interop.PassAndPlayManager
import io.github.plenglin.questofcon.net.Client
import io.github.plenglin.questofcon.render.WorldRenderer
import io.github.plenglin.questofcon.ui.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

/**
 *
 */
class MPGameScreen(val client: Client) : KtxScreen {

    lateinit var batch: SpriteBatch

    lateinit var worldRenderer: WorldRenderer

    var currentPlayerInterface: PlayerInterface =  NetworkedPlayerInterface(client)

    override fun show() {
        batch = SpriteBatch()

        worldRenderer = WorldRenderer(currentPlayerInterface.world)

        UI.targetPlayerInterface = currentPlayerInterface
        UI.gridCam.zoom = 1/48f
        UI.gridCam.position.set(0f, 0f, 0f)

        UI.generateUI()

        Gdx.input.inputProcessor = InputMultiplexer(UI.stage, GridFocusManager, MapControlInputManager, PawnActionInputProcessor, RadialMenuInputManager, GridSelectionInputManager)
    }

    override fun render(delta: Float) {

        UI.update(delta)
        MapControlInputManager.update(delta)
        UI.gridCam.update()

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl20.glClearColor(0f, 0f, 0f ,1f)

        worldRenderer.shape.projectionMatrix = UI.gridCam.combined
        worldRenderer.batch.projectionMatrix = UI.gridCam.combined

        worldRenderer.render(false, *UI.shadeSets.toTypedArray())

        UI.draw()

    }

    override fun dispose() {
        batch.dispose()
        UI.dispose()
        Assets.manager.disposeSafely()
    }

    override fun resize(width: Int, height: Int) {
        UI.gridCam.setToOrtho(false, width.toFloat(), height.toFloat())
        UI.viewport.update(width, height, true)
    }

}