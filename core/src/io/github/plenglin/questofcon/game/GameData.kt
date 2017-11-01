package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.buildings.BuildingFactory
import io.github.plenglin.questofcon.game.building.buildings.BuildingMine
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {
    val spawnableUnits = listOf<PawnCreator>(
            SimplePawnCreator("grunt", 10, 3, 2, Color.MAROON),
            SimplePawnCreator("pike mech", 20, 5, 2, Color.LIGHT_GRAY),
            SimplePawnCreator("laser mech", 15, 3, 2, range = 2, color = Color.LIGHT_GRAY)
    )

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactory, BuildingMine
    )

}