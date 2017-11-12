package io.github.plenglin.questofcon

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.screen.GameScreen
import io.github.plenglin.questofcon.screen.MPConnectionScreen
import io.github.plenglin.questofcon.screen.MPGameScreen

/**
 * THE MAIN CLASS OF MAIN CLASSINESS
 */
object QuestOfCon : Game() {

    override fun create() {
        println("thread ${Thread.currentThread()}")
        println("registering GameData")
        GameData.register()

        println("loading assets")
        Textures.values().forEach { it.load() }
        TerrainTextures.values().forEach { it.load() }
        Assets.load()
        Assets.manager.finishLoading()

        println("finished loading assets")

        if (Config.mode == Config.Mode.CLIENT) {
            println("starting MP client")
            Gdx.app.postRunnable {
                println("thread ${Thread.currentThread()}")
                setScreen(MPConnectionScreen)
            }
        } else {
            println("starting pnp")
            Gdx.app.postRunnable { setScreen(GameScreen) }
        }
    }

}