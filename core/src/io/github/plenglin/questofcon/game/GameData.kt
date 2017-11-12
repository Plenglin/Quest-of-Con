package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.ObjectRegistry
import io.github.plenglin.questofcon.TerrainTextures
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.BuildingFactory
import io.github.plenglin.questofcon.game.building.BuildingHQ
import io.github.plenglin.questofcon.game.building.BuildingMine
import io.github.plenglin.questofcon.game.grid.Biome
import io.github.plenglin.questofcon.game.pawn.PawnArtillery
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.PawnKnight
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {

    val pawns = ObjectRegistry<PawnCreator>()
    val buildings = ObjectRegistry<BuildingCreator>()
    val biomes = ObjectRegistry<Biome>()

    val grunt = SimplePawnCreator("inf-grunt", "grunt", 100).apply {
        maxHealth = 30
        attack = 20
        texture = { Assets[Assets.grunt] }
    }

    val drill = SimplePawnCreator("mech-drill","drill mech", 150).apply {
        attack = 40
        maxHealth = 50
        texture = { Assets[Assets.drillmech] }
    }

    val beam = SimplePawnCreator("mech-laser", "laser mech", 150).apply {
        attack = 30
        maxHealth = 40
        range = 2
        texture = { Assets[Assets.beammech] }
    }

    val tankdes = SimplePawnCreator("veh-td", "tank destroyer", 300).apply {
        attack = 50
        maxHealth = 20
        range = 4
        actionPoints = 20
        texture = { Assets[Assets.tankdestroyer] }
    }

    val defender = SimplePawnCreator("mech-defender", "defender", 250).apply {
        attack = 30
        maxHealth = 100
        actionPoints = 2
        texture = { Assets[Assets.defender] }
    }

    val scout = SimplePawnCreator("veh-scout", "scout", 200).apply {
        attack = 20
        maxHealth = 30
        actionPoints = 5
        range = 2
        texture = { Assets[Assets.scout] }
    }

    val beach       = Biome("biome-beach", "beach", TerrainTextures.SAND, true, false)
    val desert      = Biome("biome-desert", "desert", TerrainTextures.SAND, true, false)
    val grass       = Biome("biome-grassland", "grassland", TerrainTextures.GRASS, true, true)
    val highlands   = Biome("biome-highlands", "highlands", TerrainTextures.BIGHILL, true, false, movementCost = 2)
    val water       = Biome("biome-water", "water", TerrainTextures.WATER, false, false)
    val mountains   = Biome("biome-mountains", "mountains", TerrainTextures.MOUNTAIN, false, false)

    fun register() {
        pawns.register(grunt)
        pawns.register(drill)
        pawns.register(beam)
        pawns.register(tankdes)
        pawns.register(defender)
        pawns.register(scout)
        pawns.register(PawnKnight)
        pawns.register(PawnArtillery)

        buildings.register(BuildingHQ)
        buildings.register(BuildingFactory)
        buildings.register(BuildingMine)

        biomes.register(beach)
        biomes.register(desert)
        biomes.register(grass)
        biomes.register(highlands)
        biomes.register(water)
        biomes.register(mountains)
    }

}