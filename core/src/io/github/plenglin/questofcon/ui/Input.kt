package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import io.github.plenglin.questofcon.QuestOfCon
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.screen.GameScreen
import io.github.plenglin.questofcon.screen.UIState
import ktx.app.KtxInputAdapter


object MapControlInputManager : KtxInputAdapter {

    val cam: OrthographicCamera = GameScreen.gridCam
    var vx: Int = 0
    var vy: Int = 0
    var fast: Boolean = false

    override fun scrolled(amount: Int): Boolean {
        when (amount) {
            1 -> cam.zoom = cam.zoom * QuestOfCon.zoomRate
            -1 -> cam.zoom = cam.zoom / QuestOfCon.zoomRate
        }
        cam.zoom = minOf(maxOf(cam.zoom, QuestOfCon.minZoom), QuestOfCon.maxZoom)
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.SHIFT_LEFT -> {
                fast = true
                return false
            }

            Input.Keys.W, Input.Keys.UP -> {
                vy += 1
                return true
            }
            Input.Keys.S, Input.Keys.DOWN -> {
                vy += -1
                return true
            }

            Input.Keys.A, Input.Keys.LEFT -> {
                vx += -1
                return true
            }
            Input.Keys.D, Input.Keys.RIGHT -> {
                vx += 1
                return true
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.SHIFT_LEFT -> {
                fast = false
                return false
            }

            Input.Keys.W, Input.Keys.UP -> {
                vy -= 1
                return true
            }
            Input.Keys.S, Input.Keys.DOWN -> {
                vy -= -1
                return true
            }

            Input.Keys.A, Input.Keys.LEFT -> {
                vx -= -1
                return true
            }
            Input.Keys.D, Input.Keys.RIGHT -> {
                vx -= 1
                return true
            }
        }
        return false
    }

    fun update(delta: Float) {
        val mult = if (fast) 2 else 1
        cam.translate(vx * mult * QuestOfCon.camSpeed * delta, vy * mult * QuestOfCon.camSpeed * delta)
    }

}

object GridSelectionInputManager : KtxInputAdapter {

    val cam: OrthographicCamera = GameScreen.gridCam
    val world: World = GameScreen.gameState.world
    val selectionListeners = mutableListOf<(WorldCoords?, Int, Int) -> Unit>()

    var selection: WorldCoords? = null
        private set(value) {
            if (value != null) {
                field = if (value.exists) value else null
            } else {
                field = null
            }

            if (field != null) {
                UI.tileInfo.target = field
            }

            UI.tileInfo.isVisible = (field != null)
        }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val gridPos = cam.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val i = gridPos.x.toInt()
        val j = gridPos.y.toInt()
        when (pointer) {
            Input.Buttons.LEFT -> {
                val grid = WorldCoords(world, i, j)
                selection = grid
                selectionListeners.forEach { it(selection, screenX, screenY) }
            }
        }
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                selection = null
                UI.radialMenu.isVisible = false
                UI.radialMenu.active = false
                return true
            }
        }
        return false
    }

}

object RadialMenuInputManager : KtxInputAdapter {

    val pawnMenu = listOf<Selectable>(
            Selectable("Move", { x, y ->
                println("showing pawn movement menu")
                val pawn = GridSelectionInputManager.selection!!.tile!!.pawn!!
                GameScreen.uiState = UIState.MOVING_PAWN
                GameScreen.pawnActionData = PawnAction(pawn, pawn.getMovableSquares())
            }),
            Selectable("Attack", { x, y ->
                println("showing attack menu")
                val pawn = GridSelectionInputManager.selection!!.tile!!.pawn!!
                GameScreen.uiState = UIState.ATTACKING_PAWN
                GameScreen.pawnActionData = PawnAction(pawn, pawn.getAttackableSquares())
            }),
            Selectable("Disband", { x, y ->
                println("disbanding pawn")
                UI.stage.addActor(ConfirmationDialog("Disband Pawn", UI.skin, {
                    GridSelectionInputManager.selection!!.tile!!.pawn!!.health = 0
                }))
            })

    )

    private val radialMenu = UI.radialMenu

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        when (button) {
            Input.Buttons.RIGHT -> {
                radialMenu.items = getSelectables()
                radialMenu.setPosition(screenX.toFloat(), UI.viewport.screenHeight - screenY.toFloat())
                radialMenu.updateUI()
                radialMenu.active = true
                radialMenu.isVisible = true
                return true
            }
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val sx = screenX.toFloat()
        val sy = UI.viewport.screenHeight - screenY.toFloat()
        if (radialMenu.active) {
            when (button) {
                Input.Buttons.RIGHT -> {
                    val selected = radialMenu.getSelected((sx - radialMenu.x).toDouble(), (sy - radialMenu.y).toDouble())
                    selected?.onSelected?.invoke(screenX.toFloat(), screenY.toFloat())
                    println(selected)
                    radialMenu.active = false
                    radialMenu.isVisible = false
                    return true
                }
            }
        }
        return false
    }

    private fun getSelectables(): List<Selectable> {
        val selection = GridSelectionInputManager.selection!!
        val actions = mutableListOf<Selectable>()
        val currentTeam = GameScreen.gameState.getCurrentTeam()

        val pawn = selection.tile!!.pawn
        if (pawn != null && pawn.team == currentTeam && pawn.apRemaining > 0) {
            actions.addAll(pawnMenu)
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
                        GridSelectionInputManager.selection!!
                ).show(UI.stage)
            }))
        }
        return actions
    }

}