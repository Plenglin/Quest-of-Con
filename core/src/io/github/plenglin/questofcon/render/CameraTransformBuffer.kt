package io.github.plenglin.questofcon.render

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import ktx.math.*


class CameraTransformBuffer(val camera: OrthographicCamera, val maxPositionSize: Int = 10, val maxZoomSize: Int = 20) {

    private val positions = mutableListOf<Vector3>()
    private val zooms = mutableListOf<Float>()

    var positionSum = Vector3(0f, 0f, 0f)
    var zoomSum = 0f

    fun push(position: Vector3, zoom: Float) {
        if (positions.size >= maxPositionSize) {
            positionSum - positions.removeAt(0)
        }

        if (zooms.size >= maxZoomSize) {
            zoomSum -= zooms.removeAt(0)
        }

        positions.add(position)
        zooms.add(zoom)

        positionSum.add(position)
        zoomSum += zoom
    }

    fun updateCamera() {
        camera.position.set(positionSum.cpy().div(positions.size))
        camera.zoom = zoomSum / zooms.size
    }

}