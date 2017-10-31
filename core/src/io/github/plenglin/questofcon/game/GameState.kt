package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.game.grid.World
import io.github.plenglin.questofcon.game.unit.PawnCreator
import io.github.plenglin.questofcon.game.unit.SimplePawnCreator

/**
 *
 */
class GameState {

    val world = World(16, 16)

    val spawnableUnits = listOf<PawnCreator>(
            SimplePawnCreator("footman", 3, 2),
            SimplePawnCreator("spearman", 5, 2)
    )

}