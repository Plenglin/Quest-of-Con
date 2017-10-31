package io.github.plenglin.questofcon.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.plenglin.questofcon.QuestOfCon
import io.github.plenglin.questofcon.game.Game
import io.github.plenglin.questofcon.game.render.WorldRenderer
import ktx.app.KtxScreen

/**
 *
 */
object GameScreen : KtxScreen {

    val gridCam = OrthographicCamera()
    lateinit var batch: SpriteBatch
    lateinit var shape: ShapeRenderer
    lateinit var worldRenderer: WorldRenderer

    lateinit var uiStage: Stage
    val uiViewport: Viewport = ScreenViewport()
    lateinit var mapMovement: MapMovement

    lateinit var game: Game

    override fun show() {
        batch = SpriteBatch()
        uiStage = Stage()
        uiStage.viewport = uiViewport
        shape = ShapeRenderer()
        game = Game()
        mapMovement = MapMovement(gridCam)

        worldRenderer = WorldRenderer(game.world)
        Gdx.input.inputProcessor = InputMultiplexer(mapMovement)

        gridCam.zoom = 1/32f
        gridCam.position.set(0f, 0f, 0f)
    }

    override fun render(delta: Float) {

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl20.glClearColor(0f, 0f, 0f ,1f)

        mapMovement.update(delta)

        gridCam.update()

        worldRenderer.shape.projectionMatrix = gridCam.combined
        worldRenderer.render(true)

    }

    override fun dispose() {
        batch.dispose()
        shape.dispose()
        uiStage.dispose()
    }

    override fun resize(width: Int, height: Int) {
        gridCam.setToOrtho(false, width.toFloat(), height.toFloat())
    }

}

class MapMovement(val cam: OrthographicCamera) : InputProcessor {

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
                vy = 1
                return true
            }
            Input.Keys.S, Input.Keys.DOWN -> {
                vy = -1
                return true
            }

            Input.Keys.A, Input.Keys.LEFT -> {
                vx = -1
                return true
            }
            Input.Keys.D, Input.Keys.RIGHT -> {
                vx = 1
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

            Input.Keys.W, Input.Keys.S, Input.Keys.UP, Input.Keys.DOWN -> {
                if (vy != 0) {
                    vy = 0
                    return true
                }
            }

            Input.Keys.A, Input.Keys.D, Input.Keys.LEFT, Input.Keys.RIGHT -> {
                if (vx != 0) {
                    vx = 0
                    return true
                }
            }
        }
        return false
    }

    fun update(delta: Float) {
        val mult = if (fast) 2 else 1
        cam.translate(vx * mult * QuestOfCon.camSpeed * delta, vy * mult * QuestOfCon.camSpeed * delta)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun keyTyped(character: Char) = false

}