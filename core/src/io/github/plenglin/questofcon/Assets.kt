package io.github.plenglin.questofcon

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import ktx.assets.getAsset
import ktx.assets.load


object Assets {

    val manager = AssetManager()

    val missing = AssetDescriptor<Texture>("sprites/missing.png", Texture::class.java)
    val q = AssetDescriptor<Texture>("sprites/1x1.png", Texture::class.java)

    val artillery = AssetDescriptor<Texture>("sprites/pawn/artillery.png", Texture::class.java)
    val defender = AssetDescriptor<Texture>("sprites/pawn/defender.png", Texture::class.java)
    val drillmech = AssetDescriptor<Texture>("sprites/pawn/drillmech.png", Texture::class.java)
    val grunt = AssetDescriptor<Texture>("sprites/pawn/grunt.png", Texture::class.java)
    val kangaroobot = AssetDescriptor<Texture>("sprites/pawn/kangaroobot.png", Texture::class.java)
    val scout = AssetDescriptor<Texture>("sprites/pawn/scout.png", Texture::class.java)
    val tankdestroyer = AssetDescriptor<Texture>("sprites/pawn/tankdestr.png", Texture::class.java)
    val beammech = AssetDescriptor<Texture>("sprites/pawn/beammech.png", Texture::class.java)

    fun load() {
        manager.load(artillery)
        manager.load(defender)
        manager.load(drillmech)
        manager.load(grunt)
        manager.load(kangaroobot)
        manager.load(scout)
        manager.load(tankdestroyer)
        manager.load(beammech)

        manager.load(missing)
        manager.load(q)
    }

    operator fun <T> get(descriptor: AssetDescriptor<T>): T {
        return manager[descriptor]
    }

}

enum class TerrainTextures(simple: Boolean = false) {
    GRASS, SMALLHILL, BIGHILL, SAND, WATER(true), MOUNTAIN(true);

    private val bgPath = "sprites/terrain/${name.toLowerCase()}${if (!simple) "-bg" else ""}.png"
    private val fgPath = "sprites/terrain/${name.toLowerCase()}${if (!simple) "-fg" else ""}.png"

    fun load() {
        Textures.manager.load<Texture>(bgPath)
        Textures.manager.load<Texture>(fgPath)
    }

    fun bg() = manager.getAsset<Texture>(bgPath)
    fun fg() = manager.getAsset<Texture>(fgPath)

    companion object {
        var manager: AssetManager = Assets.manager

    }
}

enum class Textures(dir: String) {
    HEADQUARTERS("building"), MINE("building"), FACTORY("building");

    val path = "sprites/$dir/${name.toLowerCase()}.png"
    fun load() = manager.load<Texture>(path)
    operator fun invoke() = manager.getAsset<Texture>(path)
    companion object {
        var manager: AssetManager = Assets.manager
    }
}
