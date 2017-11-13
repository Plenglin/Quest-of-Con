package io.github.plenglin.questofcon.ui.elements

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import io.github.plenglin.questofcon.game.PlayerInterface
import io.github.plenglin.questofcon.ui.UI

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