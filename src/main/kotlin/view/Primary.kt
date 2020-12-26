package view

import BaseStyle.Companion.primary
import controller.CastView
import controller.Client
import controller.Store
import controller.Syndication
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
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

    override val root = borderpane {
        left<Menu>()
        center = vbox {
            alignment = Pos.BOTTOM_CENTER
            scrollpane {
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                style {
                    backgroundColor += primary
                }

                isFitToHeight = true
                isFitToWidth = true

                vbox(15.0) {
                    hgrow = Priority.ALWAYS
                    alignment = Pos.CENTER
                    style {
                        padding = box(30.px, 0.px)
                    }
                    bindChildren(CastView.castScopes) {
                        find<CastFragment>(it).root
                    }
                }
            }
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
            try {
                syndication.refreshRss()
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { println(e) }
                syndication.loadExisting()
            } finally {
                syndication.downloadImages(syndication.checkMissingImages())
            }
        }
    }
}