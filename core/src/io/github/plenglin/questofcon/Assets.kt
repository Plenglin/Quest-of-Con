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

    val headquarters = AssetDescriptor<Texture>("sprites/building/headquarters.png", Texture::class.java)
    val factory = AssetDescriptor<Texture>("sprites/building/factory.png", Texture::class.java)
    val mine = AssetDescriptor<Texture>("sprites/building/mine.png", Texture::class.java)

    val bighill = AssetDescriptor<Texture>("sprites/terrain/bighill-bg.png", Texture::class.java)
    val grass = AssetDescriptor<Texture>("sprites/terrain/grass-bg.png", Texture::class.java)
    val mountain = AssetDescriptor<Texture>("sprites/terrain/mountain.png", Texture::class.java)
    val sand = AssetDescriptor<Texture>("sprites/terrain/sand-bg.png", Texture::class.java)
    val smallhill = AssetDescriptor<Texture>("sprites/terrain/smallhill-bg.png", Texture::class.java)
    val water = AssetDescriptor<Texture>("sprites/terrain/water.png", Texture::class.java)

    val missingno = { this[missing] }

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

        manager.load(headquarters)
        manager.load(factory)
        manager.load(mine)

        manager.load(bighill)
        manager.load(grass)
        manager.load(mountain)
        manager.load(sand)
        manager.load(smallhill)
        manager.load(water)
    }

    operator fun <T> get(descriptor: AssetDescriptor<T>): T {
        return manager[descriptor]
    }

}
