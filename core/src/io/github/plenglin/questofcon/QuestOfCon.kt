package io.github.plenglin.questofcon

import com.badlogic.gdx.Screen
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

}