package io.github.plenglin.questofcon.screen

import io.github.plenglin.questofcon.net.Client
import ktx.app.KtxScreen

/**
 *
 */
object MPGameScreen : KtxScreen {

    lateinit var client: Client

    fun initializeWith(client: Client) {
        this.client = client
    }

    override fun show() {

    }

}