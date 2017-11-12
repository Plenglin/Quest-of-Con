package io.github.plenglin.questofcon

import com.badlogic.gdx.Game
import io.github.plenglin.questofcon.screen.GameScreen
import io.github.plenglin.questofcon.screen.MPConnectionScreen
import io.github.plenglin.questofcon.screen.MPGameScreen

/**
 * THE MAIN CLASS OF MAIN CLASSINESS
 */
object QuestOfCon : Game() {

    override fun create() {
        Textures.values().forEach { it.load() }
        TerrainTextures.values().forEach { it.load() }
        Assets.load()
        Assets.manager.finishLoading()

        if (System.getenv("mp") == "1") {
            println("asdf")
            setScreen(MPConnectionScreen)
        } else {
            println("moo")
            setScreen(GameScreen)
        }
    }

}