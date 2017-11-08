package io.github.plenglin.questofcon

import com.badlogic.gdx.Game
import io.github.plenglin.questofcon.screen.GameScreen
import io.github.plenglin.questofcon.screen.MPGameScreen

/**
 * THE MAIN CLASS OF MAIN CLASSINESS
 */
object QuestOfCon : Game() {

    override fun create() {
        if (System.getenv("mp") == "1") {
            setScreen(MPGameScreen)
        } else {
            setScreen(GameScreen)
        }
    }

}