package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import io.github.plenglin.questofcon.QuestOfCon
import io.github.plenglin.questofcon.game.building.buildings.BuildingHQ
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.render.ShadeSet
import io.github.plenglin.questofcon.screen.GameScreen
import ktx.app.KtxInputAdapter
import sun.awt.image.URLImageSource


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

    var selectedShadeSet: ShadeSet? = null
    var hoveringShadeSet: ShadeSet? = null

    var selection: WorldCoords? = null
        private set(value) {
            GameScreen.shadeSets.remove(selectedShadeSet)
            if (value != null && value.exists) {
                field = value
                selectedShadeSet = ShadeSet(setOf(value), QuestOfCon.selectionColor)
                GameScreen.shadeSets.add(selectedShadeSet!!)
            } else {
                field = null
            }

            if (field != null) {
                UI.tileInfo.target = field
            }

            UI.tileInfo.isVisible = (field != null)
        }

    var hovering: WorldCoords? = null
        private set(value) {
            GameScreen.shadeSets.remove(hoveringShadeSet)
            if (value != null && value.exists) {
                field = value
                hoveringShadeSet = ShadeSet(setOf(value), QuestOfCon.hoveringColor)
                GameScreen.shadeSets.add(hoveringShadeSet!!)
            } else {
                field = null
            }
        }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        hovering = getGridPos(screenX, screenY)
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

    val pawnMenu = listOf<Selectable>(
            Selectable("Move", {
                PawnActionInputManager.setPawnState(
                        GridSelectionInputManager.hovering!!.tile!!.pawn!!,
                        PawnActionInputManager.State.MOVE
                )
            }),
            Selectable("Attack", {
                PawnActionInputManager.setPawnState(
                        GridSelectionInputManager.hovering!!.tile!!.pawn!!,
                        PawnActionInputManager.State.ATTACK
                )
            }),
            Selectable("Disband", {
                println("disbanding pawn")
                ConfirmationDialog("Disband Pawn", UI.skin, {
                    GridSelectionInputManager.selection!!.tile!!.pawn!!.health = 0
                }).show(UI.stage)
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
                    selected?.onSelected?.invoke()
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
        val currentTeam = GameScreen.gameState.getCurrentTeam()
        val selection = GridSelectionInputManager.hovering!!

        if (currentTeam.hasBuiltHQ) {
            val actions = mutableListOf<Selectable>()

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
                actions.add(Selectable("Build", {
                    BuildingSpawningDialog(
                            GameScreen.gameState.getCurrentTeam(),
                            UI.skin,
                            GridSelectionInputManager.hovering!!
                    ).show(UI.stage)
                }))
            }
            return actions
        } else {
            return listOf(Selectable("Build HQ", {
                BuildingHQ.createBuildingAt(currentTeam, selection)
            }))
        }
    }

}

object PawnActionInputManager : KtxInputAdapter {

    var state = State.NONE
    private lateinit var pawn: Pawn

    private var shadeSet: ShadeSet? = null
        set(value) {
            GameScreen.shadeSets.remove(field)
            if (value != null) {
                GameScreen.shadeSets.add(value)
            }
            field = value
        }

    fun setPawnState(pawn: Pawn, state: State) {
        this.pawn = pawn
        when (state) {
            State.MOVE -> {
                selectionSet = pawn.getMovableSquares()
                shadeSet = ShadeSet(selectionSet, QuestOfCon.movementColor)
            }
            State.ATTACK -> {
                selectionSet = pawn.getAttackableSquares()
                shadeSet = ShadeSet(selectionSet, QuestOfCon.attackColor)
            }
            else -> {
                shadeSet = null
            }
        }
        this.state = state
    }

    private lateinit var selectionSet: Set<WorldCoords>

    override fun keyDown(keycode: Int): Boolean {
        val pawn = GridSelectionInputManager.selection?.tile?.pawn ?: return false
        if (pawn.apRemaining <= 0) {
            return false
        }
        this.pawn = pawn
        when (keycode) {
            Input.Keys.Q -> {  // Attack
                if (state == State.ATTACK) {
                    setPawnState(pawn, State.NONE)
                    return false
                }
                setPawnState(pawn, State.ATTACK)
            }
            Input.Keys.E -> {  // Move
                if (state == State.MOVE) {
                    setPawnState(pawn, State.NONE)
                    return false
                }
                setPawnState(pawn, State.MOVE)
            }
            Input.Keys.ESCAPE -> {  // Stop what you're doing!
                setPawnState(pawn, State.NONE)
                return false
            }
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.MIDDLE) {
            return false
        }
        val hovering = GridSelectionInputManager.hovering ?: return false
        when (state) {
            State.NONE -> return false
            State.ATTACK -> {
                if (selectionSet.contains(hovering) && pawn.attemptAttack(hovering)) {
                    setPawnState(pawn, State.NONE)
                    return true
                }
            }
            State.MOVE -> {
                if (selectionSet.contains(hovering)) {
                    pawn.moveTo(hovering)
                    setPawnState(pawn, State.NONE)
                    return true
                }
            }
        }
        return false
    }

    enum class State {
        NONE, MOVE, ATTACK
    }

}