package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.BuildingFactoryCreator
import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator

/**
 *
 */
class GameState {

    val world = World(16, 16)

    val spawnableUnits = listOf<PawnCreator>(
            SimplePawnCreator("footman", 3, 2, Color.MAROON),
            SimplePawnCreator("spearman", 5, 2, Color.LIGHT_GRAY)
    )

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactoryCreator()
    )

}