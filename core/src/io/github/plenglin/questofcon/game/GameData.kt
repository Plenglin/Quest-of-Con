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

    val grunt = SimplePawnCreator("grunt", 100).apply {
        maxHealth = 30
        attack = 20
        texture = { Assets[Assets.grunt] }
    }

    val drill = SimplePawnCreator("drill mech", 150).apply {
        attack = 40
        maxHealth = 50
        texture = { Assets[Assets.drillmech] }
    }

    val beam = SimplePawnCreator("laser mech", 150).apply {
        attack = 30
        maxHealth = 40
        range = 2
        texture = { Assets[Assets.beammech] }
    }

    val tankdes = SimplePawnCreator("tank destroyer", 300).apply {
        attack = 50
        maxHealth = 20
        range = 4
        actionPoints = 20
        texture = { Assets[Assets.tankdestroyer] }
    }

    val defender = SimplePawnCreator("defender", 250).apply {
        attack = 30
        maxHealth = 100
        actionPoints = 2
        texture = { Assets[Assets.defender] }
    }

    val scout = SimplePawnCreator("scout", 200).apply {
        attack = 20
        maxHealth = 30
        actionPoints = 5
        range = 2
        texture = { Assets[Assets.scout] }
    }

    val spawnableUnits = listOf<PawnCreator>(
            grunt, drill, beam, tankdes, defender, scout,
            PawnArtillery, PawnKnight
    ).sortedBy { it.cost }

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactory, BuildingMine
    ).sortedBy { it.name }

    fun buildingByType(type: Long): BuildingCreator {
        return spawnableBuildings.find { it.id == type }!!
    }

    fun pawnByType(type: Long): PawnCreator {
        return spawnableUnits.find { it.id == type }!!
    }
}