package io.github.plenglin.questofcon

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import ktx.assets.getAsset
import ktx.assets.load


object Assets {

    val manager = AssetManager()

}

enum class TerrainTextures(simple: Boolean = false) {
    GRASS, SMALLHILL, BIGHILL, SAND, WATER(true), MOUNTAIN(true);

    val simplePath = name.toLowerCase()
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

enum class PawnTextures {
    ARTILLERY, DEFENDER, DRILLMECH, GRUNT, KANGAROOBOT, SCOUT, TANKDESTR, BEAMMECH;

    val path = "sprites/pawn/$name.png"

    fun load() = manager.load<Texture>(path)
    operator fun invoke() = manager.getAsset<Texture>(path)

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
