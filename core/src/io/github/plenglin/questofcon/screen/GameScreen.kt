package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.render.WorldRenderer
import io.github.plenglin.questofcon.ui.GridSelection
import io.github.plenglin.questofcon.ui.UI
import io.github.plenglin.questofcon.ui.MapMovement
import ktx.app.KtxScreen

/**
 *
 */
object GameScreen : KtxScreen {

    val gridCam = OrthographicCamera()
    lateinit var batch: SpriteBatch

    lateinit var worldRenderer: WorldRenderer

    lateinit var mapMovement: MapMovement
    lateinit var gridSelection: GridSelection

    lateinit var gameState: GameState

    val teamA = Team("escargot", Color.BLUE)
    val teamB = Team("parfait", Color.WHITE)
    val teamC = Team("la baguette", Color.RED)

    override fun show() {
        batch = SpriteBatch()
        gameState = GameState(listOf(teamA, teamB, teamC))

        mapMovement = MapMovement(gridCam)
        gridSelection = GridSelection(gridCam, gameState.world)

        worldRenderer = WorldRenderer(gameState.world)
        Gdx.input.inputProcessor = InputMultiplexer(UI.stage, gridSelection, mapMovement)

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
        UI.viewport.update(width, height, true)
    }

}
