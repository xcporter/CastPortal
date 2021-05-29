package view

import BaseStyle.Companion.delete
import BaseStyle.Companion.highlight
import BaseStyle.Companion.mid
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import BaseStyle.Companion.settingsButton
import BaseStyle.Companion.simpleAdd
import BaseStyle.Companion.simpleDelete
import controller.Configuration
import controller.Store
import controller.Syndication
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.PrimaryViewModel
import model.RenderDownloads
import tornadofx.*
import java.io.File

class Settings : View(), CoroutineScope {
    override val coroutineContext = Dispatchers.IO
    val syn: Syndication by inject()
    val store : Store by inject()

    val addProperty = SimpleStringProperty()
    val errProperty = SimpleStringProperty()
    override val root = vbox(10.0) {
        fitToParentSize()
        alignment = Pos.TOP_LEFT
        style {
            padding = box(18.px)
        }
        label ("Settings") {
            textFill = midHigh
            style {
                fontSize = 3.em
            }
        }
        vbox(10.0) {
            fitToParentSize()
            style {
                padding = box(0.px, 20.px)
            }
            label ("Rss Feeds") {
                textFill = highlight
                style {
                    fontSize = 2.em
                }
                graphic = FontAwesomeIconView(FontAwesomeIcon.RSS).apply {
                    fill = highlight
                    size = "2em"
                }
            }
            scrollpane {
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                fitToParentWidth()
                style {
                    backgroundColor += primary
                }
                vbox(6.0) {
                    minWidth = 500.0
                    style {
                        padding = box(0.px, 10.px)
                    }
                    bindChildren(syn.rssLinks) {
                        hbox {
                            hgrow = Priority.ALWAYS
                            style {
                                backgroundColor += mid
                                padding = box(8.px)
                            }
                            text(it) {
                                fill = highlight
                            }
                            region { hgrow = Priority.ALWAYS }
                            button {
                                addClass(simpleDelete)
                                graphic = FontAwesomeIconView(FontAwesomeIcon.TIMES).apply {
                                    fill = c("#C24C2B")
                                    size = "2em"
                                }
                                action {
                                    launch {
                                        syn.removeRss(it)
                                        syn.loadExisting()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            hbox(8.0) {
                alignment = Pos.CENTER_LEFT
                style {
                    padding = box(0.px, 10.px)
                }
                textfield(addProperty) {
                    prefWidth = 300.0
                }
                button {
                    addClass(simpleAdd)
                    graphic = FontAwesomeIconView(FontAwesomeIcon.PLUS).apply {
                        fill = highlight
                        size = "2em"
                    }
                    action {
                        if (!syn.rssLinks.contains(addProperty.value)) {
                            launch {
                                PrimaryViewModel.isLoadNew.value = true
                                if (syn.validateStream(addProperty.value)) {
                                    withContext(Dispatchers.Main) {
                                        syn.rssLinks.add(addProperty.value)
                                        errProperty.value = ""
                                    }
                                    store.saveSubscription(addProperty.value)
                                    withContext(Dispatchers.Main) { addProperty.value = "" }
                                    try {
                                        syn.refreshRss()
                                    } catch (e: Throwable) {
                                        withContext(Dispatchers.Main) { println(e) }
                                        syn.loadExisting()
                                    } finally {
                                        syn.downloadImages(syn.checkMissingImages())
                                    }
                                } else { withContext(Dispatchers.Main) { errProperty.value = "invalid RSS link" } }
                                PrimaryViewModel.isLoadNew.value = false
                            }
                        } else { addProperty.value = "" }
                    }
                }
                label (errProperty) {
                    textFill = highlight
                    style {
                        fontSize = 1.em
                    }
                }
                progressindicator {
                    prefHeight = 20.0
                    prefWidth = 20.0
                    visibleWhen(PrimaryViewModel.isLoadNew)
                    managedWhen(PrimaryViewModel.isLoadNew)
                }
            }
            vbox(8.0) {
                style {
                    padding = box(2.em, 0.em, 0.em, 0.em)
                }
                button("Clear Downloads") {
                    addClass(settingsButton)
                    action {
                        warn("Warning, this will remove all downloaded episodes") {
                            File("${Configuration.path.path}/downloads/").listFiles()?.forEach { it.delete() }
                            PrimaryViewModel.castScopes
                                .flatMap { it.model.downloaded.clear(); it.model.items }
                                .forEach { it.isDownload.value = false }
                            fire(RenderDownloads())
                        }
                    }
                }
                button ("Clear all stored data") {
                    addClass(delete)
                    action {
                        warn("Warning, this will remove all saved downloads, RSS streams, and settings") {
                            File(Configuration.path.path).deleteRecursively()
                        }
                    }
                }
            }
        }
    }

    private fun warn(warning: String, action: () -> Unit) {
        PrimaryViewModel.warning.value = warning
        PrimaryViewModel.warnAction = action
        openInternalWindow(Warning::class, movable = false)
    }
}