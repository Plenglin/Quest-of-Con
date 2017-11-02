package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.BuildingFactory
import io.github.plenglin.questofcon.game.building.BuildingMine
import io.github.plenglin.questofcon.game.pawn.PawnArtillery
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.PawnKnight
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {
    val grunt = SimplePawnCreator("grunt", 10, 3, 2, Color.MAROON)
    val pike = SimplePawnCreator("pike mech", 20, 5, 4, Color.LIGHT_GRAY)
    val laser = SimplePawnCreator("laser mech", 15, 3, 3, range = 2, color = Color.RED)
    val sniper = SimplePawnCreator("tank destroyer", 30, 2, 5, color = Color.ROYAL, actionPoints = 1, range = 4)
    val defender = SimplePawnCreator("defender", 25, 10, 3, color = Color.FIREBRICK, actionPoints = 2)
    val scout = SimplePawnCreator("scout", 15, 3, 2, actionPoints = 5, range = 2, color = Color.CYAN)

    val spawnableUnits = listOf<PawnCreator>(
            grunt, pike, laser, sniper, defender, scout,
            PawnArtillery, PawnKnight
    ).sortedBy { it.name }

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactory, BuildingMine
    ).sortedBy { it.name }

}