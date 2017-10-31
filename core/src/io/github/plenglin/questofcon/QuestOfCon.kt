package io.github.plenglin.questofcon

import com.badlogic.gdx.Screen
import io.github.plenglin.questofcon.screen.GameScreen
import ktx.app.KtxGame

/**
 *
 */
object QuestOfCon : KtxGame<Screen>(GameScreen) {

    val zoomRate = 1.5f
    val camSpeed = 7f

}