package io.github.plenglin.questofcon

import com.badlogic.gdx.Game
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.screen.GameScreen
import io.github.plenglin.questofcon.screen.MPConnectionScreen
import io.github.plenglin.questofcon.screen.MPGameScreen

/**
 * THE MAIN CLASS OF MAIN CLASSINESS
 */
object QuestOfCon : Game() {

    override fun create() {
        GameData.register()

        Textures.values().forEach { it.load() }
        TerrainTextures.values().forEach { it.load() }
        Assets.load()
        Assets.manager.finishLoading()

        if (Config.mode == Config.Mode.CLIENT) {
            println("asdf")
            setScreen(MPConnectionScreen)
        } else {
            println("moo")
            setScreen(GameScreen)
        }
    }

}