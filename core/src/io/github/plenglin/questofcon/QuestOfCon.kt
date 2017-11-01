package io.github.plenglin.questofcon

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.screen.GameScreen
import ktx.app.KtxGame

/**
 *
 */
object QuestOfCon : KtxGame<Screen>(GameScreen) {

    val zoomRate = 1.125f
    val minZoom = 1/64f
    val maxZoom = 1/8f

    val camSpeed = 7f

    val selectionColor = Color(0.5f, 0.75f, 0f, 0.5f)
    val movementColor: Color = Color(0f, 0f, 1f, 0.5f)
    val attackColor: Color = Color(1f, 0f, 0f, 0.5f)
    val STARTING_MONEY: Int = 50
    val BASE_ECO = 10

}