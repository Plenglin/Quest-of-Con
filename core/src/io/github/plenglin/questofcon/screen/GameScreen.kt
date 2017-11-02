package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.plenglin.questofcon.QuestOfCon
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

    var pawnActionData: PawnAction? = null
    var uiState = UIState.NONE

    val teamA = Team("escargot", Color.BLUE)
    val teamB = Team("parfait", Color.WHITE)
    val teamC = Team("le baguette", Color.RED)

    override fun show() {
        batch = SpriteBatch()
        gameState = GameState(listOf(teamA, teamB, teamC))

        GameData.spawnableBuildings[0].createBuildingAt(teamA, WorldCoords(gameState.world, 5, 5))

        worldRenderer = WorldRenderer(gameState.world)

        gridCam.zoom = 1/32f
        gridCam.position.set(0f, 0f, 0f)

        UI.generateUI()

        var previous: WorldCoords? = null
        /*
        gridSelection.selectionListeners.add({ selection, screenX, screenY ->

            when (uiState) {
                UIState.NONE -> {
                    // Detected double click
                    if (selection != null && selection == previous) {
                        println("showing radial menu")
                        UI.radialMenu.apply {
                            val actions = mutableListOf<Selectable>()
                            val currentTeam = gameState.getCurrentTeam()

                            val pawn = selection.tile!!.pawn
                            if (pawn != null && pawn.team == currentTeam && pawn.apRemaining > 0) {
                                actions.addAll(RadialMenus.pawnMenu)
                            }

                            val building = selection.tile.building
                            println("building enabled: ${building?.enabled}")
                            if (building != null && building.team == currentTeam && building.enabled) {
                                actions.addAll(selection.tile.building!!.getActions())
                            }
                            if (selection.tile.canBuildOn(currentTeam)) {
                                actions.add(Selectable("Build", { x, y ->
                                    BuildingSpawningDialog(
                                            GameScreen.gameState.getCurrentTeam(),
                                            UI.skin,
                                            GameScreen.gridSelection.selection!!
                                    ).show(UI.stage)
                                }))
                            }
                            println(actions)
                            items = actions
                            radiusX = 50f
                            radiusY = 25f
                            isVisible = true
                            active = true
                            updateUI()
                            setPosition(screenX.toFloat(), (Gdx.graphics.height - screenY).toFloat())
                            println("$x, $y")
                        }
                    } else {
                        println("hiding radial menu")
                        UI.radialMenu.isVisible = false
                        UI.radialMenu.active = false
                    }
                }
                UIState.MOVING_PAWN -> {
                    val pawnData = pawnActionData!!
                    if (selection != null && pawnData.squares.contains(selection)) {
                        pawnData.pawn.moveTo(selection)
                        uiState = UIState.NONE
                    }
                }
                UIState.ATTACKING_PAWN -> {
                    val pawnData = pawnActionData!!
                    if (selection != null && pawnData.squares.contains(selection)) {
                        pawnData.pawn.attack(selection)
                        uiState = UIState.NONE
                        UI.tileInfo.updateData()
                    }
                }
            }

            previous = gridSelection.selection
        })*/
        Gdx.input.inputProcessor = InputMultiplexer(UI.stage, RadialMenuInputManager, GridSelectionInputManager, MapControlInputManager)
    }

    override fun render(delta: Float) {

        UI.update(delta)
        MapControlInputManager.update(delta)
        gridCam.update()

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl20.glClearColor(0f, 0f, 0f ,1f)

        worldRenderer.shape.projectionMatrix = gridCam.combined

        /*
        when (uiState) {
            UIState.MOVING_PAWN -> {
                shadeSets.add(ShadeSet(pawnActionData!!.squares, QuestOfCon.movementColor))
            }
            UIState.ATTACKING_PAWN -> shadeSets.add(ShadeSet(pawnActionData!!.squares, QuestOfCon.attackColor))
            UIState.NONE -> {}
        }*/
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

enum class UIState {
    NONE, MOVING_PAWN, ATTACKING_PAWN
}