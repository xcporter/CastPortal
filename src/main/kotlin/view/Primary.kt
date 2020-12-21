package view

import BaseStyle.Companion.primary
import controller.Client
import controller.Store
import controller.Syndication
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.SyndicationModel
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class Primary() : View ("Cast Portal"), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    val syndication: Syndication by inject()
    val store: Store by inject()
    val client: Client by inject()

    override val root = borderpane {
        left<Menu>()
        center = scrollpane {
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
                    bindChildren(syndication.rssFeeds) {
                        find<CastFragment>("model" to SyndicationModel(it)).root
                    }
                }
//                flowpane() {
//                    style {
//                        padding = box (30.px)
//                        backgroundColor += primary
//                    }
//                    hgap = 20.0
//                    vgap = 20.0
//                    alignment = Pos.CENTER
//                    bindChildren(syndication.rssFeeds) {
//                        vbox {
//                            style {
//                                backgroundColor += c("#FFFFFF")
//                                padding = box(10.px)
//                            }
//                            text(it.channel?.title ?: "No title")
//                            text(it.channel?.author ?: "No Author")
//                            text("${ it.channel?.items?.size } episodes")
//                        }
//                    }
//                }

        }

        bottom<Player>()
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