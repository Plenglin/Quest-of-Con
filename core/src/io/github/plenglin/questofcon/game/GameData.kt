package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.buildings.BuildingFactory
import io.github.plenglin.questofcon.game.building.buildings.BuildingMine
import io.github.plenglin.questofcon.game.pawn.PawnArtillery
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.PawnKnight
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {
    val grunt = SimplePawnCreator("grunt", 10, 3, 2, Color.MAROON)
    val pike = SimplePawnCreator("pike mech", 20, 5, 4, Color.LIGHT_GRAY)
    val laser = SimplePawnCreator("laser mech", 15, 3, 3, range = 2, color = Color.RED)
    val spawnableUnits = listOf<PawnCreator>(
            grunt, pike, laser,
            PawnArtillery,
            PawnKnight
    )

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactory, BuildingMine
    )

}