package view

import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import model.PrimaryViewModel
import controller.Client
import controller.Playback
import controller.Store
import controller.Syndication
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class Primary() : View ("Cast Portal"), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    val syndication: Syndication by inject()
    val store: Store by inject()
    val client: Client by inject()

    val container = vbox {
        vgrow = Priority.ALWAYS
        alignment = Pos.CENTER
        add<Home>()
    }

    init {
        PrimaryViewModel.viewState.onChange {
            it?.let {
                container.replaceChildren (
                    when(it) {
                        ViewState.HOME -> find<Home>().root
                        ViewState.DOWNLOADS -> find<Downloads>().root
                        ViewState.SETTINGS -> find<Settings>().root
                    }
                )
            }
        }
    }

    override val root = borderpane {
        shortcut("space") {
            if (Playback.isPlaying.value) Playback.player?.pause()
            else Playback.player?.play()
        }
        left<Menu>()
        center = vbox {
            fitToParentSize()
            add(container)
            add<Player>()
        }


        bottom {
            hbox {
                style = """
                -fx-border-style: solid none none none;
                -fx-border-color: orange;
                -fx-border-width: 2;
                -fx-padding: 2px;
            """
                region { hgrow = Priority.ALWAYS }
                text("©2020 Podcast Farm Inc. - All Rights Reserved") {
                    style {
                        fill = Color.WHITE
                        fontSize = 10.px
                    }
                }
            }
        }
    }

    override fun onBeforeShow() {
        launch {
            PrimaryViewModel.isDownloadRss.value = true
            try {
                syndication.refreshRss()
            } catch (e: Throwable) {
                println(e)
                syndication.loadExisting()
            } finally {
                PrimaryViewModel.isDownloadRss.value = false
                syndication.downloadImages(syndication.checkMissingImages())
            }
        }
    }
}