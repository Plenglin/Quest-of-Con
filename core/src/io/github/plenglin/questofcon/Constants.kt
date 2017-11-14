package io.github.plenglin.questofcon

import com.badlogic.gdx.graphics.Color


object Constants {

    val zoomRate = 1.125f
    val minZoom = 1/256f
    val maxZoom = 1/8f

    val camSpeed = 7f

    val selectionColor = Color(1f, 1f, 1f, 0.5f)
    val hoveringColor = Color(1f, 1f, 1f, 0.75f)
    val movementColor: Color = Color(0f, 0f, 1f, 0.25f)
    val attackColor: Color = Color(1f, 0f, 0f, 0.5f)

    val STARTING_MONEY: Int = 500
    val BASE_ECO: Int = 100
    val HQ_HEALTH = 500
    val ELEVATION_LEVELS = 6
    val ELEVATION_DEF_BONUS_PER_DELTA = 0.1

    val SERVER_PORT = 51337

}