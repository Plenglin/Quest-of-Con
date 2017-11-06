package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.game.building.BuildingHQ
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.render.CameraTransformBuffer
import io.github.plenglin.questofcon.render.ShadeSet
import io.github.plenglin.questofcon.screen.GameScreen
import ktx.app.KtxInputAdapter


object MapControlInputManager : KtxInputAdapter {

    val cam: OrthographicCamera = GameScreen.gridCam
    val buffer = CameraTransformBuffer(cam)

    var zoomTarget = Constants.minZoom
    val positionTarget = Vector2(0f, 0f)

    var vx: Int = 0
    var vy: Int = 0
    var fast: Boolean = false

    override fun scrolled(amount: Int): Boolean {
        when (amount) {
            1 -> zoomTarget *= Constants.zoomRate
            -1 -> zoomTarget /= Constants.zoomRate
        }
        zoomTarget = minOf(maxOf(zoomTarget, Constants.minZoom), Constants.maxZoom)
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
        println("$vx, $vy")
        positionTarget.add(vx * mult * Constants.camSpeed * delta, vy * mult * Constants.camSpeed * delta)
        buffer.push(Vector3(positionTarget.x, positionTarget.y, 0f), zoomTarget)
        buffer.updateCamera()
        GridSelectionInputManager.mouseMoved(Gdx.input.x, Gdx.input.y)
    }

}

object GridSelectionInputManager : KtxInputAdapter {

    val cam: OrthographicCamera = GameScreen.gridCam
    val world: World = GameScreen.gameState.world
    val selectionListeners = mutableListOf<(WorldCoords?, Int, Int) -> Unit>()

    var selectedShadeSet: ShadeSet? = null
    var hoveringShadeSet: ShadeSet? = null
    var attackableShadeSet: ShadeSet? = null

    var selection: WorldCoords? = null
        private set(value) {
            GameScreen.shadeSets.remove(selectedShadeSet)
            if (value != null && value.exists) {
                field = value
                selectedShadeSet = ShadeSet(setOf(value), Constants.selectionColor)
                GameScreen.shadeSets.add(selectedShadeSet!!)
            } else {
                field = null
            }
        }

    var hovering: WorldCoords? = null
        private set(value) {
            GameScreen.shadeSets.remove(hoveringShadeSet)
            if (value != null && value.exists) {
                field = value
                hoveringShadeSet = ShadeSet(setOf(value), mode = ShadeSet.OUTLINE, lines = Constants.hoveringColor)
                GameScreen.shadeSets.add(hoveringShadeSet!!)
            } else {
                field = null
            }

            if (field != null) {
                UI.tileInfo.target = field
            }

            UI.tileInfo.isVisible = (field != null)
        }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        hovering = getGridPos(screenX, screenY)
        val pawn = hovering?.tile?.pawn
        GameScreen.shadeSets.remove(attackableShadeSet)
        if (pawn != null) {
            attackableShadeSet = ShadeSet(pawn.getAttackableSquares(), mode = ShadeSet.OUTLINE, lines = Constants.attackColor)
            GameScreen.shadeSets.add(attackableShadeSet!!)
        } else {
            attackableShadeSet = null
        }
        UI.updateData()
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        when (pointer) {
            Input.Buttons.LEFT -> {
                val grid = getGridPos(screenX, screenY)
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

    fun getGridPos(screenX: Int, screenY: Int): WorldCoords {
        val gridPos = cam.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val i = gridPos.x.toInt()
        val j = gridPos.y.toInt()
        return WorldCoords(world, i, j)
    }

}

object RadialMenuInputManager : KtxInputAdapter {

    lateinit var selectedCoord: WorldCoords

    private val radialMenu = UI.radialMenu

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        when (button) {
            Input.Buttons.RIGHT -> {
                val hov = GridSelectionInputManager.hovering
                if (hov != null) {
                    selectedCoord = hov
                    radialMenu.items = getSelectables()
                    radialMenu.setPosition(screenX.toFloat(), UI.viewport.screenHeight - screenY.toFloat())
                    radialMenu.updateUI()
                    radialMenu.active = true
                    radialMenu.isVisible = true
                    return true
                }
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
                    selected?.onSelected?.invoke(selectedCoord)
                    radialMenu.active = false
                    radialMenu.isVisible = false
                    return true
                }
            }
        }
        return false
    }

    private fun getSelectables(): List<Selectable> {
        val currentTeam = GameScreen.gameState.getCurrentTeam()
        val selection = GridSelectionInputManager.hovering ?: return emptyList()

        if (currentTeam.hasBuiltHQ) {

            val actions = mutableListOf<Selectable>()

            // Pawn actions
            val pawn = selection.tile!!.pawn
            if (pawn != null && pawn.team == currentTeam) {
                actions.addAll(pawn.getRadialActions())
            }

            // Building actions
            val building = selection.tile.building
            if (building != null && building.team == currentTeam && building.enabled) {
                actions.addAll(building.getRadialActions())
            }

            // Construction actions
            if (selection.tile.canBuildOn(currentTeam)) {
                actions.add(Selectable("Build", {
                    BuildingSpawningDialog(
                            GameScreen.gameState.getCurrentTeam(),
                            UI.skin,
                            it
                    ).show(UI.stage)
                }))
            }

            return actions

        } else {
            return if (selection.tile?.canBuildOn(currentTeam) == true)
                listOf(Selectable("Build HQ", {
                    BuildingHQ.createBuildingAt(currentTeam, selection)
                }))
            else emptyList()
        }
    }

}
