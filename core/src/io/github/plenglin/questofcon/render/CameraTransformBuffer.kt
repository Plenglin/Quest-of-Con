package io.github.plenglin.questofcon.render

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import ktx.math.*


class CameraTransformBuffer(val camera: OrthographicCamera, val maxSize: Int = 100) {

    private val positions = mutableListOf<Vector3>()
    private val zooms = mutableListOf<Float>()

    var positionSum = Vector3(0f, 0f, 0f)
    var zoomSum = 0f

    fun push(position: Vector3, zoom: Float) {
        if (positions.size >= maxSize) {
            positionSum - positions.removeAt(0)
            zoomSum -= zooms.removeAt(0)
        }

        positions.add(position)
        zooms.add(zoom)

        positionSum + camera.position
        zoomSum += camera.zoom
    }

    fun updateCamera() {
        camera.position.set(positionSum).div(positions.size)
        camera.zoom = zoomSum / zooms.size
    }

}