package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import io.github.plenglin.questofcon.QuestOfCon
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.grid.WorldCoords


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

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun keyTyped(character: Char) = false

}

class GridSelection(val cam: OrthographicCamera, val world: World) : InputProcessor {

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
            println(field)
        }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val gridPos = cam.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val i = gridPos.x.toInt()
        val j = gridPos.y.toInt()
        when (pointer) {
            Input.Buttons.LEFT -> {
                val grid = WorldCoords(world, i, j)
                // Detected double click
                if (selection != null && selection == grid) {
                    println("showing radial menu")
                    UI.radialMenu.setPosition(screenX.toFloat(), (Gdx.graphics.height - screenY).toFloat())
                    UI.radialMenu.isVisible = true
                    UI.radialMenu.active = true
                } else {
                    UI.radialMenu.isVisible = false
                    UI.radialMenu.active = false
                }
                selection = grid
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

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun keyTyped(character: Char) = false

    override fun scrolled(amount: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

}