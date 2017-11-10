package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.GameState
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.BuildingFactory
import io.github.plenglin.questofcon.game.grid.*
import io.github.plenglin.questofcon.render.WorldRenderer
import io.github.plenglin.questofcon.ui.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

/**
 *
 */
object GameScreen : KtxScreen {

    lateinit var batch: SpriteBatch

    lateinit var worldRenderer: WorldRenderer

    lateinit var gameState: GameState

    val teamA = Team("escargot", Color.BLUE)
    val teamB = Team("parfait", Color.WHITE)
    val teamC = Team("le baguette", Color.RED)

    override fun show() {
        batch = SpriteBatch()
        gameState = GameState(listOf(teamA, teamB, teamC))
        println("Generating terrain...")

        println("Generating height data...")
        val heightData = HeightMap(DiamondSquareHeightGenerator(3, initialOffsets = 2.0, iterativeRescale = 0.8).generate().grid).normalized
        val rainfallData = HeightMap(DiamondSquareHeightGenerator(3, initialOffsets = 2.0, iterativeRescale = 0.8).generate().grid).normalized

        heightData.grid.forEach { col ->
            col.forEach {
                print("%.2f\t".format(it))
            }
            println()
        }

        println("Mapping height data to world...")
        MapToHeight(gameState.world, heightData).doHeightMap()

        println("Adding biomes...")
        BiomeGenerator(gameState.world, heightData, rainfallData).applyBiomes()

        /*GameData.scout.createPawnAt(teamA, WorldCoords(gameState.world, 5, 5))
        BuildingFactory.createBuildingAt(teamA, WorldCoords(gameState.world, 5, 5))
        BuildingFactory.createBuildingAt(teamB, WorldCoords(gameState.world, 7, 5))
        println(WorldCoords(gameState.world, 7, 5).tile!!.passableBy(teamA))
        println(WorldCoords(gameState.world, 7, 5).tile!!.passableBy(teamB))*/

        worldRenderer = WorldRenderer(gameState.world)

        UI.gridCam.zoom = 1/48f
        UI.gridCam.position.set(0f, 0f, 0f)

        UI.generateUI()

        Gdx.input.inputProcessor = InputMultiplexer(UI.stage, MapControlInputManager, PawnActionInputProcessor, RadialMenuInputManager, GridSelectionInputManager)
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