package io.github.plenglin.questofcon.ui.elements

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.GridSelectionInputManager
import io.github.plenglin.questofcon.ui.PawnActionManager
import io.github.plenglin.questofcon.ui.PawnActionState
import io.github.plenglin.questofcon.ui.UI

class TileInfoPanel(skin: Skin) : Table(skin) {

    var target: WorldCoords? = null
        set(value) {
            field = value
            updateData()
        }

    val titleLabel = Label("", skin)
    val pawn = PropertiesTable(skin)
    val building = PropertiesTable(skin)

    init {
        add(titleLabel).colspan(2)
        row()
        add(Label("Unit", skin)).center().pad(10f)
        add(pawn).top().left().expandX().pad(5f)
        row()
        add(Label("Building", skin)).center().pad(10f)
        add(building).top().left().expandX().pad(5f)
        pad(5f)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        top().left()

        super.draw(batch, parentAlpha)
    }

    fun updateData() {
        val coord = target
        if (coord?.tile != null) {
            titleLabel.setText("${coord.tile.elevation} high ${coord.tile.biome.name.capitalize()} at ${coord.i}, ${coord.j}  (Cost: ${coord.tile.biome.movementCost})")

            val pawn = coord.tile.pawn
            this.pawn.data = pawn?.getProperties() ?: emptyMap()

            val building = coord.tile.building
            this.building.data = building?.getProperties() ?: emptyMap()
        }

        pawn.updateData()
        building.updateData()
        pack()
    }

}

class PropertiesTable(skin: Skin) : Table(skin) {

    var data: Map<String, Any> = mapOf()

    fun updateData() {
        clearChildren()
        if (data.isNotEmpty()) {
            data.forEach { k, v ->
                add(Label(k.capitalize(), skin)).left().prefWidth(100f)
                add(Label(v.toString(), skin)).right()
                row()
            }
        } else {
            add(Label("none", skin)).colspan(2).center()
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        top().left()
        super.draw(batch, parentAlpha)
    }

}

class GameStateInfoController(skin: Skin) : Window("Status", skin) {

    val playerInterface get(): PlayerInterface = UI.targetPlayerInterface
    val currentTeamLabel: Label
    val moneyLabel: Label
    val ecoLabel: Label
    val nextTurnButton: TextButton

    init {
        currentTeamLabel = Label("", skin)
        moneyLabel = Label("", skin)
        ecoLabel = Label("", skin)
        nextTurnButton = TextButton("Next Turn", skin)
        nextTurnButton.addListener(
                object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        playerInterface.sendEndTurn()
                        updateData()
                    }
                }
        )

        add(currentTeamLabel)
        add(nextTurnButton)
        row()
        add(moneyLabel)
        add(ecoLabel)
        pack()
    }

    fun updateData() {
        val team = playerInterface.getCurrentTeam()
        val isCurrentTeam = (team == playerInterface.thisTeam)

        currentTeamLabel.setText("${team.name}'s turn")
        moneyLabel.setText("$${team.money}")
        ecoLabel.setText("+$${team.getMoneyPerTurn()}")

        moneyLabel.isVisible = isCurrentTeam
        ecoLabel.isVisible = isCurrentTeam
        nextTurnButton.isVisible = isCurrentTeam
        pack()
    }

}

class ActionTooltip(skin: Skin) : Table(skin) {

    val a: Label = Label("", skin)
    val b: Label = Label("", skin)
    val c: Label = Label("", skin)
    val d: Label = Label("", skin)

    init {
        add(a).width(100f).left()
        add(b).right()
        row()
        add(c).left()
        add(d).right()
    }

    fun updateData() {
        val thePawn = PawnActionManager.pawn ?: return
        when (PawnActionManager.state) {
            PawnActionState.NONE -> this.isVisible = false
            PawnActionState.MOVE -> {
                val hov = GridSelectionInputManager.hovering
                if (hov != null) {
                    val cost = PawnActionManager.movementSquares[hov]
                    if (cost != null) {
                        isVisible = true
                        a.setText(hov.tile!!.biome.name.capitalize())
                        b.setText("$cost")
                        c.setText("Actions")
                        d.setText("${thePawn.ap} -> ${thePawn.ap - cost}")
                    } else {
                        this.isVisible = false
                    }
                }
            }
            PawnActionState.ATTACK -> {
                val target = GridSelectionInputManager.hovering
                if (target != null) {
                    isVisible = true
                    val ptarget = target.tile!!.pawn
                    val building = target.tile.building
                    val damage = thePawn.damageTo(target)
                    a.setText(ptarget?.name ?: "")
                    b.setText(if (ptarget != null) "${ptarget.health} -> ${ptarget.health - damage}" else "")
                    c.setText(building?.name ?: "")
                    d.setText(if (building != null) "${building.health} -> ${building.health - damage}" else "")
                } else {
                    isVisible = false
                }
            }
        }
        pack()
    }

}