package io.github.plenglin.questofcon.net

import io.github.plenglin.questofcon.game.GameData


fun main(args: Array<String>) {
    println("Starting server...")
    GameData.buildings.forEach {
        println("${it.id}: ${it.name}")
    }
    Matchmaker.acceptSockets()
}