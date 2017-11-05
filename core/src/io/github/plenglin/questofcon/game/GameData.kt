package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.BuildingFactory
import io.github.plenglin.questofcon.game.building.BuildingMine
import io.github.plenglin.questofcon.game.pawn.PawnArtillery
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.PawnKnight
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {

    val grunt = SimplePawnCreator("grunt", 10).apply {
        maxHealth = 3
        attack = 2
        texture = { Assets[Assets.grunt] }
    }

    val drill = SimplePawnCreator("drill mech", 20).apply {
        attack = 4
        maxHealth = 5
        texture = { Assets[Assets.drillmech] }
    }

    val beam = SimplePawnCreator("laser mech", 15).apply {
        attack = 3
        maxHealth = 4
        range = 2
        texture = { Assets[Assets.beammech] }
    }

    val sniper = SimplePawnCreator("tank destroyer", 30).apply {
        attack = 5
        maxHealth = 2
        range = 4
        actionPoints = 2
        texture = { Assets[Assets.tankdestroyer] }
    }

    val defender = SimplePawnCreator("defender", 25).apply {
        attack = 3
        maxHealth = 10
        actionPoints = 2
        texture = { Assets[Assets.defender] }
    }

    val scout = SimplePawnCreator("scout", 15).apply {
        attack = 2
        maxHealth = 3
        actionPoints = 5
        range = 2
        texture = { Assets[Assets.scout] }
    }

    val spawnableUnits = listOf<PawnCreator>(
            grunt, drill, beam, sniper, defender, scout,
            PawnArtillery, PawnKnight
    ).sortedBy { it.title }

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactory, BuildingMine
    ).sortedBy { it.name }

}