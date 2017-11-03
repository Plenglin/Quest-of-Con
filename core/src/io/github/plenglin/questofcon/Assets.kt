package io.github.plenglin.questofcon

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import ktx.assets.getAsset
import ktx.assets.load


object Assets {

    val manager = AssetManager()

}

enum class Textures(dir: String) {
    HEADQUARTERS("building"), MINE("building"), FACTORY("building"),
    GRASS("terrain"), SMALLHILL("terrain"), BIGHILL("terrain"), WATER("terrain"), MOUNTAIN("terrain");

    val path = "sprites/$dir/${name.toLowerCase()}.png"
    fun load() = manager.load<Texture>(path)
    operator fun invoke() = manager.getAsset<Texture>(path)
    companion object {
        var manager: AssetManager = Assets.manager
    }
}
