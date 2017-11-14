package io.github.plenglin.questofcon.game

import io.github.plenglin.questofcon.Assets
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.game.building.BuildingType
import io.github.plenglin.questofcon.game.grid.Biome
import io.github.plenglin.questofcon.game.pawn.PawnClass
import io.github.plenglin.questofcon.game.pawn.PawnType
import io.github.plenglin.util.ObjectRegistry


object GameData {

    val pawns = ObjectRegistry<PawnType>()
    val buildings = ObjectRegistry<BuildingType>()
    val biomes = ObjectRegistry<Biome>()

    val grunt = PawnType("inf-grunt").apply {
        displayName = "grunt"
        cost = 75
        baseHp = 30
        baseAtk = 20
        type = PawnClass.INFANTRY
        texture = { Assets[Assets.grunt] }
    }

    val drill = PawnType("mech-drill").apply {
        displayName = "drill mech"
        cost = 150
        baseHp = 50
        baseAtk = 40
        type = PawnClass.MECH
        texture = { Assets[Assets.drillmech] }
    }

    val beam = PawnType("mech-beam").apply {
        displayName = "beam mech"
        cost = 150
        baseHp = 40
        baseAtk = 30
        maxRange = 2
        type = PawnClass.MECH
        texture = { Assets[Assets.drillmech] }
    }

    val tankdes = PawnType("veh-td").apply {
        displayName = "tank destroyer"
        cost = 300
        baseAtk = 30
        baseHp = 20
        maxRange = 4
        maxAp = 2
        type = PawnClass.VEHICLE
        texture = { Assets[Assets.tankdestroyer] }
    }

    val defender = PawnType("mech-defender").apply {
        displayName = "defender"
        cost = 250
        baseAtk = 30
        baseHp = 100
        maxAp = 2
        type = PawnClass.MECH
        texture = { Assets[Assets.defender] }
    }

    val scout = PawnType("veh-scout").apply {
        displayName = "scout"
        cost = 200
        baseAtk = 20
        baseHp = 30
        maxAp = 5
        maxRange = 2
        type = PawnClass.VEHICLE
        texture = { Assets[Assets.scout] }
    }

    val artillery = PawnType("mech-artillery").apply {
        displayName = "artillery"
        cost = 500
        baseAtk = 50
        baseHp = 20
        maxAp = 1
        maxRange = 5
        minRange = 4
        targetRadius = 1
        type = PawnClass.MECH
        texture = { Assets[Assets.artillery] }
    }

    val hq = BuildingType("bldg-hq").apply {
        displayName = "headquarters"
        maxHp = 1000
        upkeep = -Constants.BASE_ECO
        powerConsumption = -100
        texture = { Assets[Assets.headquarters] }
    }

    val factory = BuildingType("bldg-factory").apply {
        displayName = "factory"
        maxHp = 100
        cost = 150
        upkeep = 0
        powerConsumption = 10
        texture = { Assets[Assets.factory] }
        buildable = { pawns.toList() }
    }

    val mine = BuildingType("bldg-mine").apply {
        displayName = "mine"
        maxHp = 75
        cost = 200
        upkeep = -30
        powerConsumption = 10
        texture = { Assets[Assets.mine] }
    }

    val beach       = Biome("biome-beach", "beach", true, false, { Assets[Assets.sand] })
    val desert      = Biome("biome-desert", "desert", true, false, { Assets[Assets.sand] })
    val grass       = Biome("biome-grassland", "grassland", true, true, { Assets[Assets.grass] })
    val highlands   = Biome("biome-highlands", "highlands", true, false, { Assets[Assets.bighill] }, movementCost = 2)
    val water       = Biome("biome-water", "water", false, false, { Assets[Assets.water] })
    val mountains   = Biome("biome-mountains", "mountains", false, false, { Assets[Assets.mountain] })
    val flattened   = Biome("biome-flattened", "flattened", true, true, { Assets[Assets.smallhill] })

    fun register() {
        pawns.register(grunt)
        pawns.register(drill)
        pawns.register(beam)
        pawns.register(tankdes)
        pawns.register(defender)
        pawns.register(scout)
        pawns.register(artillery)

        buildings.register(hq)
        buildings.register(factory)
        buildings.register(mine)

        biomes.register(beach)
        biomes.register(desert)
        biomes.register(grass)
        biomes.register(highlands)
        biomes.register(water)
        biomes.register(mountains)
    }

}