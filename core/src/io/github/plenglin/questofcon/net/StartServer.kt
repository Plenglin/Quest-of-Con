package io.github.plenglin.questofcon.net

import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.GameData


fun main(args: Array<String>) {
    println("Starting server...")
    GameData.spawnableBuildings.forEach {
        println("${it.id}: ${it.name}")
    }
    Server.acceptSockets()
}