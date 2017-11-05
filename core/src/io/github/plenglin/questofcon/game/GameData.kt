package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.PawnTextures
import io.github.plenglin.questofcon.game.GameData.grunt
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.BuildingFactory
import io.github.plenglin.questofcon.game.building.BuildingMine
import io.github.plenglin.questofcon.game.pawn.PawnArtillery
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.PawnKnight
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {
    val grunt = SimplePawnCreator("grunt", 10, 3, 2, texture = PawnTextures.GRUNT)
    val drill = SimplePawnCreator("drill mech", 20, 5, 4, texture = PawnTextures.DRILLMECH)
    val beam = SimplePawnCreator("laser mech", 15, 3, 3, range = 2, texture = PawnTextures.BEAMMECH)
    val sniper = SimplePawnCreator("tank destroyer", 30, 2, 5, texture = PawnTextures.TANKDESTR, actionPoints = 1, range = 4)
    val defender = SimplePawnCreator("defender", 25, 10, 3, texture = PawnTextures.DEFENDER, actionPoints = 2)
    val scout = SimplePawnCreator("scout", 15, 3, 2, actionPoints = 5, range = 2, texture = PawnTextures.SCOUT)

    val spawnableUnits = listOf<PawnCreator>(
            grunt, drill, beam, sniper, defender, scout,
            PawnArtillery, PawnKnight
    ).sortedBy { it.name }

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactory, BuildingMine
    ).sortedBy { it.name }

}