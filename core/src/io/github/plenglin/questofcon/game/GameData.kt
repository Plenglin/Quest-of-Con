package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.buildings.BuildingFactory
import io.github.plenglin.questofcon.game.building.buildings.BuildingMine
import io.github.plenglin.questofcon.game.pawn.PawnArtillery
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {
    val spawnableUnits = listOf<PawnCreator>(
            SimplePawnCreator("grunt", 10, 3, 2, Color.MAROON),
            SimplePawnCreator("pike mech", 20, 5, 4, Color.LIGHT_GRAY),
            SimplePawnCreator("laser mech", 15, 3, 3, range = 2, color = Color.LIGHT_GRAY),
            PawnArtillery
    )

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactory, BuildingMine
    )

}