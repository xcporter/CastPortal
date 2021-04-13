package view

import model.PrimaryViewModel
import controller.Client
import controller.Playback
import controller.Store
import controller.Syndication
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import kotlinx.coroutines.*
import model.ShowDescription
import tornadofx.*
import view.fragments.CastFragment
import view.fragments.CastInfoFragment
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
                        ViewState.DETAIL -> find<CastFragment>(PrimaryViewModel.detailView.value).root
                        ViewState.DETAIL_DOWNLOAD -> find<CastFragment>(PrimaryViewModel.detailView.value, mapOf("downloadsOnly" to true)).root
                    }
                )
            }
        }

        subscribe<ShowDescription> {
            openInternalWindow<CastInfoFragment>(
                icon = hbox {
                    style { padding = box(0.px, 10.px) }
                    text("${it.title} by ${it.author}") {
                        style { fontSize = 2.em }
                        wrappingWidth = 400.0
                    }
                },
                movable = false,
                params = mapOf("info" to it.description)
            )
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
                PrimaryViewModel.setError("unable to access new rss feed(s); using existing")
                syndication.loadExisting()
            } finally {
                PrimaryViewModel.isDownloadRss.value = false
                syndication.downloadImages(syndication.checkMissingImages())
            }
        }
    }
}