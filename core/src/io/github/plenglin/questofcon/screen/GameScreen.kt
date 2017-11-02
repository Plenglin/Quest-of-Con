package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.render.ShadeSet
import io.github.plenglin.questofcon.render.WorldRenderer
import io.github.plenglin.questofcon.ui.*
import ktx.app.KtxScreen

/**
 *
 */
object GameScreen : KtxScreen {

    val gridCam = OrthographicCamera()
    lateinit var batch: SpriteBatch

    lateinit var worldRenderer: WorldRenderer

    lateinit var gameState: GameState

    val shadeSets = mutableListOf<ShadeSet>()

    val teamA = Team("escargot", Color.BLUE)
    val teamB = Team("parfait", Color.WHITE)
    val teamC = Team("le baguette", Color.RED)

    override fun show() {
        batch = SpriteBatch()
        gameState = GameState(listOf(teamA, teamB, teamC))

        GameData.spawnableBuildings[0].createBuildingAt(teamA, WorldCoords(gameState.world, 5, 5))

        worldRenderer = WorldRenderer(gameState.world)

        gridCam.zoom = 1/48f
        gridCam.position.set(0f, 0f, 0f)

        UI.generateUI()

        Gdx.input.inputProcessor = InputMultiplexer(UI.stage, MapControlInputManager, PawnActionInputManager, RadialMenuInputManager, GridSelectionInputManager)
    }

    override fun render(delta: Float) {

        UI.update(delta)
        MapControlInputManager.update(delta)
        gridCam.update()

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl20.glClearColor(0f, 0f, 0f ,1f)

        worldRenderer.shape.projectionMatrix = gridCam.combined

        worldRenderer.render(true, *shadeSets.toTypedArray())

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